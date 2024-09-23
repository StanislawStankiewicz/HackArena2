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
        System.out.println("🎉 Successfully connected to the server");
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
                            System.out.println("Skipping packet due to previous packet not being processed yet");
                            return;
                        }
                        try {
                            processPacket(packet);
                        } finally {
                            semaphore.release();
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
            switch (packet.getType()) {
                case PING:
                    Packet pongPacket = new Packet(PacketType.PONG, packet.getPayload());
                    String messageToSend = new ObjectMapper().writeValueAsString(pongPacket);
                    this.send(messageToSend);
                    break;

                case LOBBY_DATA:
                    if (this.agent != null) {
                        break;
                    }

                    LobbyData lobbyData = this.mapper.readValue(packet.getPayload().toString(), LobbyData.class);
                    this.agent = new MyAgent(lobbyData);
                    System.out.println("🤖 Created agent");
                    break;

                case GAME_START:
                    System.out.println("🎲 Game started");
                    break;

                case GAME_STATE:
                    GameState gameState = this.mapper.readValue(packet.getPayload().toString(), GameState.class);
                    AgentResponse response = this.agent.nextMove(gameState);
                    this.send(this.mapper.writeValueAsString(response));
                    break;

                case GAME_END:
                    this.semaphore.acquire();
                    System.out.println("🏁 Game ended");
                    GameEnd gameEnd = this.mapper.readValue(packet.getPayload().toString(), GameEnd.class);
                    this.agent.onGameEnd(gameEnd);
                    this.semaphore.release();
                    break;

                case ALREADY_MADE_MOVEMENT:
                    System.out.println("Warning: Already made action during this tick");
                    break;

                case PONG:
                    System.out.println("Received pong packet. This should not happen");
                    break;

                default:
                    System.out.println("Received packet with unknown type: " + packet.getType());
                    break;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
