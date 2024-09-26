package com.github.INIT_SGGW.MonoTanksClient.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.INIT_SGGW.MonoTanksClient.Agent.MyAgent;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.Agent;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AgentResponse;
import com.github.INIT_SGGW.MonoTanksClient.Game.GameEnd;
import com.github.INIT_SGGW.MonoTanksClient.Game.GameState;
import com.github.INIT_SGGW.MonoTanksClient.Game.LobbyData;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.*;

public class CustomWebSocketClient extends WebSocketClient {

    private Agent agent;
    private final ObjectMapper mapper;
    private final ExecutorService executorService;
    private final Semaphore semaphore;

    public CustomWebSocketClient(URI serverUri) {
        super(serverUri);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new Jdk8Module());
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
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new Jdk8Module());
                Packet packet = mapper.readValue(message, Packet.class);

                switch (packet.getType()) {
                    case LOBBY_DATA:
                    case GAME_STATE:
                        if (!semaphore.tryAcquire()) {
                            System.out.println("[System] 🚨 Skipping packet due to previous packet not being processed yet");
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
                throw new RuntimeException(e);
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
                    String reason = packet.getPayload().get("reason").asText();
                    System.out.println("[System] 🚨 Connection rejected -> " + reason);
                    yield Optional.empty();
                }

                case LOBBY_DATA -> {
                    System.out.println("[System] 🎳 Lobby data received");
                    if (this.agent == null) {
                        LobbyData lobbyData = this.mapper.readValue(packet.getPayload().toString(), LobbyData.class);
                        this.agent = new MyAgent(lobbyData);
                        System.out.println("[System] 🤖 Created agent");
                    }
                    yield Optional.empty();
                }
                case LOBBY_DELETED -> {
                    System.out.println("[System] 🚪 Lobby deleted");
                    yield Optional.empty();
                }
                case GAME_START -> {
                    System.out.println("[System] 🎲 Game started");
                    yield Optional.empty();
                }

                case GAME_STATE -> {
                    try {
                        GameState gameState = this.mapper.readValue(packet.getPayload().toString(), GameState.class);
                        AgentResponse agentResponse = this.agent.nextMove(gameState);
                        agentResponse.payload.put("gameStateId", gameState.getId());
                        String messageToSend = this.mapper.writeValueAsString(agentResponse);
                        yield Optional.of(messageToSend);
                    } catch (RuntimeException e) {
                        System.out.println("Error while processing game state: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }

                case GAME_END -> {
                    System.out.println("🏁 Game ended");
                    GameEnd gameEnd = this.mapper.readValue(packet.getPayload().toString(), GameEnd.class);
                    this.agent.onGameEnd(gameEnd);
                    yield Optional.empty();
                }

                case PLAYER_ALREADY_MADE_ACTION_WARNING -> {
                    System.out.println("[System] 🚨 Player already made action warning");
                    yield Optional.empty();
                }
                case MISSING_GAME_STATE_ID_WARNING -> {
                    System.out.println("[System] 🚨 Missing game state ID warning");
                    yield Optional.empty();
                }
                case SLOW_RESPONSE_WARNING -> {
                    System.out.println("[System] 🚨 Slow response warning");
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
                case TANK_MOVEMENT -> Optional.empty();
                case TANK_ROTATION -> Optional.empty();
                case TANK_SHOOT -> Optional.empty();
            };

            response.ifPresent(this::send);
        } catch (JsonProcessingException e) {
            System.out.println("Error while processing packet: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
