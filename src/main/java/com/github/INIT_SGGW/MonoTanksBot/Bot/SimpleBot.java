package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.Bot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;

import java.util.Optional;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.MoveLogic.*;

public class SimpleBot extends Bot {

    private final String id;

    /**
     * Constructor for the Bot class.
     *
     * @param lobbyData Initial lobby data used to create the bot.
     */
    public SimpleBot(LobbyData lobbyData) {
        super(lobbyData);
        id = lobbyData.playerId();
        System.out.println("My name is id! " + id);
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {
        // TODO Auto-generated method stub
    }

    @Override
    public BotResponse nextMove(GameState gameState) {
        return moveForward();
    }

    @Override
    public void onWarningReceived(Warning warning, Optional<String> message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGameEnd(GameEnd gameEnd) {
        // TODO Auto-generated method stub
    }
}
