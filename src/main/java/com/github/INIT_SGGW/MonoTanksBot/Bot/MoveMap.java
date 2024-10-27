package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.AbilityType;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MoveMap {

    public static final Map<Action, BotResponse> moveToResponse = new HashMap<>();

    static {
        moveToResponse.put(Action.MOVE_FORWARD, BotResponse.createMoveResponse(MoveDirection.FORWARD));
        moveToResponse.put(Action.MOVE_BACKWARD, BotResponse.createMoveResponse(MoveDirection.BACKWARD));
        moveToResponse.put(Action.ROTATE_TANK_LEFT,
                BotResponse.createRotationResponse(
                        Optional.of(RotationDirection.LEFT),
                        Optional.empty()
                ));
        moveToResponse.put(Action.ROTATE_TANK_RIGHT,
                BotResponse.createRotationResponse(
                        Optional.of(RotationDirection.RIGHT),
                        Optional.empty()
                ));
        moveToResponse.put(Action.ROTATE_TURRET_LEFT,
                BotResponse.createRotationResponse(
                        Optional.empty(),
                        Optional.of(RotationDirection.LEFT)
                ));
        moveToResponse.put(Action.ROTATE_TURRET_RIGHT,
                BotResponse.createRotationResponse(
                        Optional.empty(),
                        Optional.of(RotationDirection.RIGHT)
                ));
        moveToResponse.put(Action.USE_FIRE_BULLET, BotResponse.createAbilityUseResponse(AbilityType.FIRE_BULLET));
        moveToResponse.put(Action.USE_FIRE_DOUBLE_BULLET, BotResponse.createAbilityUseResponse(AbilityType.FIRE_DOUBLE_BULLET));
        moveToResponse.put(Action.USE_LASER, BotResponse.createAbilityUseResponse(AbilityType.USE_LASER));
        moveToResponse.put(Action.USE_RADAR, BotResponse.createAbilityUseResponse(AbilityType.USE_RADAR));
        moveToResponse.put(Action.DROP_MINE, BotResponse.createAbilityUseResponse(AbilityType.DROP_MINE));
        moveToResponse.put(Action.PASS, BotResponse.createPassResponse());
    }
}