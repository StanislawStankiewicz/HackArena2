package com.github.INIT_SGGW.MonoTanksBot.websocket;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.github.INIT_SGGW.MonoTanksBot.Bot.MyBot2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.INIT_SGGW.MonoTanksBot.Bot.MyBot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.Bot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.ConnectionRejected;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.CustomWarning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.ReadyToReceiveGameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;

public class CustomWebSocketClient extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomWebSocketClient.class);

    private Bot bot;
    private final ObjectMapper mapper;
    private final ExecutorService executorService;
    private final Semaphore semaphore;

    public CustomWebSocketClient(URI serverUri) {
        super(serverUri);

        // Increase buffer size to 1MB
        this.setReceiveBufferSize(1 * 1024 * 1024);

        // Disable connection lost timeout
        this.setConnectionLostTimeout(0);

        this.mapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool();
        this.semaphore = new Semaphore(1);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("🎉 Successfully connected to the server");
    }

    @Override
    public void onMessage(String message) {
        executorService.submit(() -> {
            try {
                Packet packet = this.mapper.readValue(message, Packet.class);
                processPacket(packet);
            } catch (JsonProcessingException e) {
                logger.error("🚨 Error processing message: {}", message);
                logger.error("🔍 Exception details:", e);
            } catch (Exception e) {
                logger.error("🚨 Unexpected error processing message: {}", message);
                logger.error("🔍 Exception details:", e);
            }
        });
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("🔌 Connection closed: {}", reason);

        // Shutdown the executor service
        executorService.shutdownNow();

        logger.info("🚪 WebSocket client shutdown completed");
    }

    @Override
    public void onError(Exception ex) {
        logger.error("🚨 WebSocket error occurred", ex);
    }

    private void processPacket(Packet packet) {
        try {
            switch (packet.getType()) {
                case PING, CONNECTION_REJECTED, CONNECTION_ACCEPTED, GAME_NOT_STARTED, GAME_IN_PROGRESS, GAME_STARTED,
                        INVALID_PACKET_TYPE_ERROR, INVALID_PACKET_USAGE_ERROR -> {
                    // These cases don't interact with the bot, so we don't need the semaphore
                    handleNonBotPacket(packet);
                }
                case LOBBY_DATA, GAME_STARTING, GAME_STATE, GAME_ENDED, PLAYER_ALREADY_MADE_ACTION_WARNING,
                        MISSING_GAME_STATE_ID_WARNING, SLOW_RESPONSE_WARNING, ACTION_IGNORED_DUE_TO_DEAD_WARNING,
                        CUSTOM_WARNING -> {
                    // These cases interact with the bot, so we need to use the semaphore
                    if (semaphore.tryAcquire(1, TimeUnit.SECONDS)) {
                        try {
                            handleBotPacket(packet);
                        } finally {
                            semaphore.release();
                        }
                    } else {
                        logger.warn("🚨 Skipping packet due to timeout waiting for semaphore: {}", packet.getType());
                    }
                }
                default -> logger.warn("Unexpected packet type in processPacket: {}", packet.getType());
            }
        } catch (JsonProcessingException e) {
            logger.error("🚨 Error while processing packet: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error("🚨 Interrupted while waiting for semaphore", e);
            Thread.currentThread().interrupt();
        }
    }

    private void handleNonBotPacket(Packet packet) throws JsonProcessingException {
        switch (packet.getType()) {
            case PING -> {
                Packet pongPacket = new Packet(PacketType.PONG, this.mapper.createObjectNode());
                String messageToSend = this.mapper.writeValueAsString(pongPacket);
                sendMessage(messageToSend);
            }
            case CONNECTION_REJECTED -> {
                ConnectionRejected connectionRejected = this.mapper.readValue(packet.getPayload().toString(),
                        ConnectionRejected.class);
                logger.warn("🚫 Connection rejected -> {}", connectionRejected.reason());
            }
            case CONNECTION_ACCEPTED -> {
                logger.info("🎉 Connection accepted");
                sendMessage(this.mapper.writeValueAsString(new Packet(PacketType.LOBBY_DATA_REQUEST, null)));
            }
            case GAME_NOT_STARTED -> logger.info("🏁 Game not started");
            case GAME_IN_PROGRESS -> logger.info("🏁 Game in progress");
            case GAME_STARTED -> logger.info("🏁 Game started");
            case INVALID_PACKET_TYPE_ERROR -> logger.error("🚨 Invalid packet type error");
            case INVALID_PACKET_USAGE_ERROR -> logger.error("🚨 Invalid packet usage error");
        }
    }

    private void handleBotPacket(Packet packet) throws JsonProcessingException {
        switch (packet.getType()) {
            case LOBBY_DATA -> {
                logger.info("🎳 Lobby data received");
                LobbyData lobbyData = this.mapper.readValue(packet.getPayload().toString(), LobbyData.class);
                if (this.bot == null) {
                    this.bot = new MyBot2(lobbyData);
                    logger.info("🤖 Created bot");

                    if (lobbyData.serverSettings().sandboxMode()) {
                        logger.info("🏜️ Sandbox mode enabled");
                        sendMessage(this.mapper
                                .writeValueAsString(new Packet(PacketType.READY_TO_RECEIVE_GAME_STATE, null)));
                        sendMessage(this.mapper.writeValueAsString(new Packet(PacketType.GAME_STATUS_REQUEST, null)));
                    }
                } else {
                    this.bot.onSubsequentLobbyData(lobbyData);
                }
            }
            case GAME_STARTING -> {
                logger.info("🎲 Game starting");

                if (this.bot == null) {
                    logger.warn("🤖 Bot is not initialized yet. Waiting for initialization...");
                    executorService.submit(() -> {
                        while (this.bot == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                logger.error("🚨 Interrupted while waiting for bot initialization", e);
                                return;
                            }
                        }
                        try {
                            sendMessage(this.mapper.writeValueAsString(new ReadyToReceiveGameState()));
                            logger.info("🚀 Sent ReadyToReceiveGameState after bot initialization");
                        } catch (JsonProcessingException e) {
                            logger.error("🚨 Error while sending ReadyToReceiveGameState", e);
                        }
                    });
                } else {
                    sendMessage(this.mapper.writeValueAsString(new ReadyToReceiveGameState()));
                }
            }
            case GAME_STATE -> {
                JsonNode payload = packet.getPayload();
                GameState gameState = this.mapper.treeToValue(payload, GameState.class);
                BotResponse botResponse = this.bot.nextMove(gameState);

                botResponse.payload.put("gameStateId", gameState.id());
                String messageToSend = this.mapper.writeValueAsString(botResponse);

                sendMessage(messageToSend);
            }
            case GAME_ENDED -> {
                logger.info("🏁 Game ended");
                GameEnd gameEnd = this.mapper.readValue(packet.getPayload().toString(), GameEnd.class);
                this.bot.onGameEnd(gameEnd);
            }
            case PLAYER_ALREADY_MADE_ACTION_WARNING ->
                this.bot.onWarningReceived(Warning.PLAYER_ALREADY_MADE_ACTION_WARNING, Optional.empty());
            case MISSING_GAME_STATE_ID_WARNING ->
                this.bot.onWarningReceived(Warning.MISSING_GAME_STATE_ID_WARNING, Optional.empty());
            case SLOW_RESPONSE_WARNING ->
                this.bot.onWarningReceived(Warning.SLOW_RESPONSE_WARNING, Optional.empty());
            case ACTION_IGNORED_DUE_TO_DEAD_WARNING ->
                this.bot.onWarningReceived(Warning.ACTION_IGNORED_DUE_TO_DEAD_WARNING, Optional.empty());
            case CUSTOM_WARNING -> {
                CustomWarning customWarning = this.mapper.readValue(packet.getPayload().toString(),
                        CustomWarning.class);
                this.bot.onWarningReceived(Warning.CUSTOM_WARNING, Optional.of(customWarning.message()));
            }
        }
    }

    private void sendMessage(String message) {
        try {
            send(message);
        } catch (Exception e) {
            logger.error("🚨 Error sending message: {}", message, e);
        }
    }
}
