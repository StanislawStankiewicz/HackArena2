package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.MoveType;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.AbilityType;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

public class MoveLogic {

    public static final Map<MoveType, Callable<BotResponse>> moveMap = new HashMap<>();

    static {
        moveMap.put(MoveType.MOVE_FORWARD, MoveLogic::moveForward);
        moveMap.put(MoveType.MOVE_BACKWARD, MoveLogic::moveBackward);
        moveMap.put(MoveType.ROTATE_TANK_LEFT, MoveLogic::rotateTankLeft);
        moveMap.put(MoveType.ROTATE_TANK_RIGHT, MoveLogic::rotateTankRight);
        moveMap.put(MoveType.ROTATE_TURRET_LEFT, MoveLogic::rotateTurretLeft);
        moveMap.put(MoveType.ROTATE_TURRET_RIGHT, MoveLogic::rotateTurretRight);
        moveMap.put(MoveType.USE_FIRE_BULLET, MoveLogic::useFireBullet);
        moveMap.put(MoveType.USE_FIRE_DOUBLE_BULLET, MoveLogic::useFireDoubleBullet);
        moveMap.put(MoveType.USE_LASER, MoveLogic::useLaser);
        moveMap.put(MoveType.USE_RADAR, MoveLogic::useRadar);
        moveMap.put(MoveType.DROP_MINE, MoveLogic::dropMine);
        moveMap.put(MoveType.PASS, MoveLogic::pass);
    }

    public static BotResponse moveForward() {
        return BotResponse.createMoveResponse(MoveDirection.FORWARD);
    }

    public static BotResponse moveBackward() {
        return BotResponse.createMoveResponse(MoveDirection.BACKWARD);
    }

    public static BotResponse rotateTankLeft() {
        return BotResponse.createRotationResponse(
                Optional.of(RotationDirection.LEFT),
                Optional.empty()
        );
    }

    public static BotResponse rotateTankRight() {
        return BotResponse.createRotationResponse(
                Optional.of(RotationDirection.RIGHT),
                Optional.empty()
        );
    }

    public static BotResponse rotateTurretLeft() {
        return BotResponse.createRotationResponse(
                Optional.empty(),
                Optional.of(RotationDirection.LEFT)
        );
    }

    public static BotResponse rotateTurretRight() {
        return BotResponse.createRotationResponse(
                Optional.empty(),
                Optional.of(RotationDirection.RIGHT)
        );
    }

    public static BotResponse rotateTankAndTurret(RotationDirection tankRotation, RotationDirection turretRotation) {
        return BotResponse.createRotationResponse(
                Optional.ofNullable(tankRotation),
                Optional.ofNullable(turretRotation)
        );
    }

    public static BotResponse useFireBullet() {
        return BotResponse.createAbilityUseResponse(    AbilityType.FIRE_BULLET);
    }

    public static BotResponse useFireDoubleBullet() {
        return BotResponse.createAbilityUseResponse(AbilityType.FIRE_DOUBLE_BULLET);
    }

    public static BotResponse useLaser() {
        return BotResponse.createAbilityUseResponse(AbilityType.USE_LASER);
    }

    public static BotResponse useRadar() {
        return BotResponse.createAbilityUseResponse(AbilityType.USE_RADAR);
    }

    public static BotResponse dropMine() {
        return BotResponse.createAbilityUseResponse(AbilityType.DROP_MINE);
    }

    public static BotResponse pass() {
        return BotResponse.createPassResponse();
    }
}