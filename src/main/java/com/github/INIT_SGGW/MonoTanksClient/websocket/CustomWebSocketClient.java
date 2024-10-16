package com.github.INIT_SGGW.MonoTanksClient.websocket;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.INIT_SGGW.MonoTanksClient.Agent.MyAgent;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.Agent;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AgentResponse;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.ConnectionRejected;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.CustomWarning;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.ReadyToReceiveGameState;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData.LobbyData;

public class CustomWebSocketClient extends WebSocketClient {

    private Agent agent;
    private final ObjectMapper mapper;
    private final ExecutorService executorService;
    private final Semaphore semaphore;

    public CustomWebSocketClient(URI serverUri) {
        super(serverUri);

        // Increase buffer size to 16KB
        this.setReceiveBufferSize(16 * 1024);

        // Disable connection lost timeout
        this.setConnectionLostTimeout(0);

        this.mapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool();
        this.semaphore = new Semaphore(1);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("[System] 🎉 Successfully connected to the server");
    }

    @Override
    public void onMessage(String message) {
        executorService.submit(() -> {
            try {
                Packet packet = this.mapper.readValue(message, Packet.class);

                switch (packet.getType()) {
                    case LOBBY_DATA:
                    case GAME_STARTING:
                    case GAME_STATE:
                    case PLAYER_ALREADY_MADE_ACTION_WARNING:
                    case MISSING_GAME_STATE_ID_WARNING:
                    case SLOW_RESPONSE_WARNING:
                    case ACTION_IGNORED_DUE_TO_DEAD_WARNING:
                    case CUSTOM_WARNING:
                        if (!semaphore.tryAcquire()) {
                            System.out.println(
                                    "[System] 🚨 Skipping packet due to previous packet not being processed yet");
                            return;
                        }
                        try {
                            processPacket(packet);
                        } finally {
                            semaphore.release();
                        }
                        break;

                    case GAME_END:
                        try {
                            semaphore.acquire();
                            processPacket(packet);
                            semaphore.release();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    default:
                        processPacket(packet);
                        break;
                }

            } catch (JsonProcessingException e) {
                System.err.println("Error processing message: " + message);
                System.err.println("Exception details:");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Unexpected error processing message: " + message);
                System.err.println("Exception details:");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void processPacket(Packet packet) {
        try {
            Optional<String> response = switch (packet.getType()) {
                case PING -> {
                    Packet pongPacket = new Packet(PacketType.PONG, this.mapper.createObjectNode());
                    String messageToSend = this.mapper.writeValueAsString(pongPacket);
                    yield Optional.of(messageToSend);
                }

                case CONNECTION_ACCEPTED -> {
                    System.out.println("[System] 🎉 Connection accepted");
                    yield Optional.empty();
                }
                case CONNECTION_REJECTED -> {
                    ConnectionRejected connectionRejected = this.mapper.readValue(packet.getPayload().toString(),
                            ConnectionRejected.class);
                    System.out.println("[System] 🚨 Connection rejected -> " + connectionRejected.reason());
                    yield Optional.empty();
                }

                case LOBBY_DATA -> {
                    System.out.println("[System] 🎳 Lobby data received");
                    LobbyData lobbyData = this.mapper.readValue(packet.getPayload().toString(), LobbyData.class);
                    if (this.agent == null) {
                        this.agent = new MyAgent(lobbyData);
                        System.out.println("[System] 🤖 Created agent");
                    } else {
                        this.agent.onSubsequentLobbyData(lobbyData);
                    }
                    yield Optional.empty();
                }
                case LOBBY_DELETED -> {
                    System.out.println("[System] 🚪 Lobby deleted");
                    yield Optional.empty();
                }
                case GAME_STARTING -> {
                    System.out.println("[System] 🎲 Game starting");
                    this.agent.onGameStarting();
                    yield Optional.of(this.mapper.writeValueAsString(new ReadyToReceiveGameState()));
                }
                case GAME_STARTED -> {
                    System.out.println("[System] 🎲 Game started");
                    yield Optional.empty();
                }

                case GAME_STATE -> {
                    try {
                        JsonNode payload = packet.getPayload();

                        GameState gameState = this.mapper.treeToValue(payload, GameState.class);
                        AgentResponse agentResponse = this.agent.nextMove(gameState);

                        agentResponse.payload.put("gameStateId", gameState.id());
                        String messageToSend = this.mapper.writeValueAsString(agentResponse);

                        yield Optional.of(messageToSend);
                    } catch (Exception e) {
                        System.err.println("Error in GAME_STATE case:");
                        e.printStackTrace();
                        yield Optional.empty();
                    }
                }

                case GAME_END -> {
                    System.out.println("🏁 Game ended");
                    GameEnd gameEnd = this.mapper.readValue(packet.getPayload().toString(), GameEnd.class);
                    this.agent.onGameEnd(gameEnd);
                    yield Optional.empty();
                }

                case PLAYER_ALREADY_MADE_ACTION_WARNING -> {
                    this.agent.onWarningReceived(Warning.PLAYER_ALREADY_MADE_ACTION_WARNING, Optional.empty());
                    yield Optional.empty();
                }
                case MISSING_GAME_STATE_ID_WARNING -> {
                    this.agent.onWarningReceived(Warning.MISSING_GAME_STATE_ID_WARNING, Optional.empty());
                    yield Optional.empty();
                }
                case SLOW_RESPONSE_WARNING -> {
                    this.agent.onWarningReceived(Warning.SLOW_RESPONSE_WARNING, Optional.empty());
                    yield Optional.empty();
                }
                case ACTION_IGNORED_DUE_TO_DEAD_WARNING -> {
                    this.agent.onWarningReceived(Warning.ACTION_IGNORED_DUE_TO_DEAD_WARNING, Optional.empty());
                    yield Optional.empty();
                }
                case CUSTOM_WARNING -> {
                    CustomWarning customWarning = this.mapper.readValue(packet.getPayload().toString(),
                            CustomWarning.class);
                    this.agent.onWarningReceived(Warning.CUSTOM_WARNING, Optional.of(customWarning.message()));
                    yield Optional.empty();
                }

                case INVALID_PACKET_TYPE_ERROR -> {
                    System.out.println("[System] 🚨 Invalid packet type error");
                    yield Optional.empty();
                }
                case INVALID_PACKET_USAGE_ERROR -> {
                    System.out.println("[System] 🚨 Invalid packet usage error");
                    yield Optional.empty();
                }

                // Should never happen
                case PONG -> Optional.empty();
                case MOVEMENT -> Optional.empty();
                case ROTATION -> Optional.empty();
                case ABILITY_USE -> Optional.empty();
                case PASS -> Optional.empty();
                case READY_TO_RECEIVE_GAME_STATE -> Optional.empty();
            };

            response.ifPresent(this::send);

        } catch (JsonProcessingException e) {
            System.out.println("Error while processing packet: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
