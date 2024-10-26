package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.AbilityType;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class MoveLogic {

    public static List<Callable<BotResponse>> moves = List.of(
            MoveLogic::moveForward,
            MoveLogic::moveBackward,
            MoveLogic::rotateTankLeft,
            MoveLogic::rotateTankRight,
            MoveLogic::rotateTurretLeft,
            MoveLogic::rotateTurretRight,
            MoveLogic::useFireBullet,
            MoveLogic::useFireDoubleBullet,
            MoveLogic::useLaser,
            MoveLogic::useRadar,
            MoveLogic::dropMine,
            MoveLogic::pass
    );

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
        return BotResponse.createAbilityUseResponse(AbilityType.FIRE_BULLET);
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