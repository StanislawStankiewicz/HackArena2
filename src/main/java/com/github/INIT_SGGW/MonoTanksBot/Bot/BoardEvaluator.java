package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.util.BoardPrinter;
import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.DirectionWrapper;
import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.ZoneWrapper;
import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.TankWrapper;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.*;

public class BoardEvaluator {
    public static int evaluateBoard(Board board) {
        int score = 0;
        TankWrapper ourTank = board.getTank(MyBot.id);
        //evaluate zones
        score += evaluateZones(board, ourTank);

        //evaluate bullets
        for (TankWrapper tank : board.getTanks()) {
            int bulletCount = board.countBulletsFlyingAt(tank);
            if (tank.getId().equals(MyBot.id)) {
                score -= DODGE_BULLET * bulletCount; // us getting shot at
            } else {
                score += SHOOT_ENEMY * bulletCount; // other people getting shot at
            }
        }

        //evaluate looking at enemy
        DirectionWrapper direction = ourTank.getTurretDirection();
        DirectionWrapper closestEnemyDirection = directionToClosestEnemy(ourTank, board);
        if (closestEnemyDirection != null) {
            int rotations = countRotationsToFace(direction, closestEnemyDirection);
            score -= ROTATE_TURRET_TOWARDS_ENEMY * rotations;
        }

        return score;
    }


    public static DirectionWrapper directionToClosestEnemy(TankWrapper tank, Board board) {
        TankWrapper closestEnemy = board.findClosestEnemy(tank);
        if (closestEnemy == null) {
            return null;
        }
        int dx = closestEnemy.getX() - tank.getX();
        int dy = closestEnemy.getY() - tank.getY();
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? DirectionWrapper.RIGHT : DirectionWrapper.LEFT;
        } else {
            return dy > 0 ? DirectionWrapper.DOWN : DirectionWrapper.UP;
        }
    }

    public static DirectionWrapper directionToZone(TankWrapper tank, ZoneWrapper zone) {
        int dx = zone.getX() - tank.getX();
        int dy = zone.getY() - tank.getY();
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? DirectionWrapper.RIGHT : DirectionWrapper.LEFT;
        } else {
            return dy > 0 ? DirectionWrapper.DOWN : DirectionWrapper.UP;
        }
    }

    public static int countRotationsToFace(DirectionWrapper direction, DirectionWrapper targetDirection) {
        int current = direction.ordinal();
        int target = targetDirection.ordinal();
        int diff = target - current;
        if (diff < 0) {
            diff += 4;
        }
        return diff;
    }

    public static int evaluateZones(Board board, TankWrapper tank) {
        int score = 0;

        // find closest zone
        ZoneWrapper[] closestZones = board.getClosestZones(tank);

        for (ZoneWrapper zone : closestZones) {
            int distance = board.getDistanceTable().distances.get(tank.getX(), tank.getY()).get(zone);
//            if (zone.getStatus() == ZoneWrapper.Status.NEUTRAL)

            if (zone.owner() == ZoneWrapper.OwnedBy.US) {
                score += GO_TOWARDS_OUR_ZONE * distance;
            } else {
                //add points for facing the zone
                DirectionWrapper direction = tank.getTurretDirection();
                DirectionWrapper closestZoneDirection = directionToZone(tank, zone);
                int rotations = countRotationsToFace(direction, closestZoneDirection);
                //print info about rotation
                System.out.println("tank direction: " + direction + " closest zone direction: " + closestZoneDirection + " rotations: " + rotations);
                score += ROTATE_BODY_TOWARDS_ZONE * rotations;

                switch (zone.owner()) {
                    case ENEMY -> score -= GO_TOWARDS_ENEMY_ZONE * distance;
                    case NOONE -> score -= GO_TOWARDS_EMPTY_ZONE * distance;
                }
                return score;
            }

        }
        return score;
    }
}
