package com.github.INIT_SGGW.MonoTanksBot.Bot.PlanBUtils;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.AbilityType;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Decider {
    private String myId;
    private GameState gameState;
    private Tile.Tank myTank;
    private Pos myTankPos;
    private List<Pos> tanksInView = new ArrayList<>();

    record Pos(int x, int y){}

    public Decider(GameState gs, String myId) {
        this.gameState = gs;
        this.myId = myId;
    }

    public void processGameState() {
        Tile[][] tiles = gameState.map().tiles();

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                Optional<Tile.TileEntity> tankTile = tiles[i][j].getEntities().stream().filter(e -> e instanceof Tile.Tank).findFirst();
                if (tankTile.isPresent()) {
                    Tile.Tank tank = (Tile.Tank) tankTile.get();
                    String id = tank.getOwnerId();
                    if (id.equals(myId)) {
                        myTank = tank;
                        myTankPos = new Pos(i, j);
                    } else {
                        tanksInView.add(new Pos(i, j));
                    }
                }

            }
        }
    }

    public boolean isEnemyInView() {
        return !tanksInView.isEmpty();
    }

    public boolean hasUncapturedZone() {
        return false;
    }

    public BotResponse attackEnemy() {
        BotResponse response = BotResponse.createPassResponse();
        if (isEnemyInFrontOfGun()) {
            response = BotResponse.createAbilityUseResponse(AbilityType.FIRE_BULLET);
        } else if (canAlignGunBetter()) {
            Direction direction = findBestGunDirection();
            response = alignTowardsDirection();
        } else if (!isPerpendicularDriveDirection()) {
            
        }
        return response;
    }

    private BotResponse alignTowardsDirection() {
//        return BotResponse.createRotationResponse(Optional.ofNullable(null), RotationDirection.);
        return BotResponse.createPassResponse();
    }

    private Direction findBestGunDirection() {
        Direction direction;
        Pos enemy = tanksInView.getFirst();
        int firstDiagCoef = enemy.y - myTankPos.y;
        int secondDiagCoef = -firstDiagCoef;

        int firstDiagX = myTankPos.x + firstDiagCoef;
        int secondDiagX = myTankPos.x + secondDiagCoef;

        if (enemy.x >= firstDiagX && enemy.x >= secondDiagX) {
            direction = Direction.DOWN;
        } else if (enemy.x <= firstDiagX && enemy.x <= secondDiagX) {
            direction = Direction.UP;
        } else if (enemy.x >= firstDiagX && enemy.x <= secondDiagX) {
            direction = Direction.LEFT;
        } else {
            direction = Direction.RIGHT;
        }

        return direction;
    }

    private boolean isPerpendicularDriveDirection() {
        return false;
    }

    private boolean canAlignGunBetter() {
        return findBestGunDirection() != myTank.getTurret().direction();
    }

    private boolean isEnemyInFrontOfGun() {
        Direction direction = myTank.getTurret().direction();

        if (direction == Direction.RIGHT) {
            return tanksInView.getFirst().x == myTankPos.x && tanksInView.getFirst().y - myTankPos.y > 0;
        } else if (direction == Direction.LEFT) {
            return tanksInView.getFirst().x == myTankPos.x && tanksInView.getFirst().y - myTankPos.y < 0;
        } else if (direction == Direction.UP) {
            return tanksInView.getFirst().y == myTankPos.y && tanksInView.getFirst().x - myTankPos.x < 0;
        } else {
            return tanksInView.getFirst().y == myTankPos.y && tanksInView.getFirst().x - myTankPos.x > 0;
        }
    }

    public BotResponse captureZone() {

        return BotResponse.createPassResponse();
    }

    public BotResponse protectZone() {
        return BotResponse.createPassResponse();
    }
}
