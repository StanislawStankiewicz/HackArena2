package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.aggressive.AggressorBot;
import com.github.INIT_SGGW.MonoTanksBot.Bot.aggressive.Point;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.Bot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MyBot extends Bot {
    private static final Logger logger = LoggerFactory.getLogger(MyBot.class);

    public static String id;
    public Point targetPoint;
    public AggressorBot bot;

    public MyBot(LobbyData lobbyData) {
        super(lobbyData);
        id = lobbyData.playerId();
        bot = new AggressorBot(id);
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {
        // TODO Auto-generated method stub
    }

    @Override
    public BotResponse nextMove(GameState gameState) {
        try {
            return MoveMap.moveToResponse.get(bot.nextMove(gameState));
        } catch (Exception e) {
            return MoveMap.moveToResponse.get(Action.PASS);
        }
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
