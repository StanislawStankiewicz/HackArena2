package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers;

import com.github.INIT_SGGW.MonoTanksBot.BasicBoard;
import com.github.INIT_SGGW.MonoTanksBot.Bot.Action;
import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.*;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class GameStateWrapper {
    private final List<ZoneWrapper> zones;

    private final List<TankWrapper> tanks = new ArrayList<>();
    private final List<ItemWrapper> items = new ArrayList<>();
    private final List<MineWrapper> mines = new ArrayList<>();
    private final List<BulletWrapper> bullets = new ArrayList<>();
    private BasicBoard<List<EntityWrapper>> tableOfEntities;

    public GameStateWrapper(GameState gameState) {
        zones = Arrays.stream(gameState.map().zones())
                .map(ZoneWrapper::new)
                .toList();
        parseGameState(gameState);
    }

    private void parseGameState(GameState gameState) {
        int height = gameState.map().tiles().length;
        int width = gameState.map().tiles()[0].length;
        tableOfEntities = new BasicBoard<>(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tableOfEntities.set(i, j, new ArrayList<>());
                for (var entity : gameState.map().tiles()[i][j].getEntities()) {
                    tableOfEntities.get(i, j).add(parse(entity, i, j));
                }
            }
        }
    }

    private EntityWrapper parse(Tile.TileEntity entity, int x, int y) {
        return switch (entity) {
            case Tile.Tank tank -> {
                TankWrapper tankWrapper = new TankWrapper(tank, x, y);
                tanks.add(tankWrapper);
                yield tankWrapper;
            }
            case Tile.Item item -> {
                ItemWrapper itemWrapper = new ItemWrapper(item, x, y);
                items.add(itemWrapper);
                yield itemWrapper;
            }
            case Tile.Bullet bullet -> {
                BulletWrapper bulletWrapper = new BulletWrapper(bullet, x, y);
                bullets.add(bulletWrapper);
                yield bulletWrapper;
            }
            case Tile.Mine mine -> new MineWrapper(mine, x, y);
            case Tile.Wall wall -> new WallWrapper(x, y);
            case Tile.Laser laser -> new LaserWrapper(laser, x, y);
            default -> null;
        };
    }

    public void perform(Action action, TankWrapper tank) {
        action.perform(this, tank);
    }

    public void moveEntity(EntityWrapper entity, int x, int y) {
        assert (entity instanceof TankWrapper || entity instanceof BulletWrapper);

        tableOfEntities.get(entity.getX(), entity.getY()).remove(entity);
        tableOfEntities.get(x, y).add(entity);
        entity.setX(x);
        entity.setY(y);
    }

    public void createBullet(BulletWrapper bullet) {
        bullets.add(bullet);
        tableOfEntities.get(bullet.getX(), bullet.getY()).add(bullet);
    }

    public void createLasers(LaserWrapper laser) {
        // recursively create lasers in the direction of the laser until it hits a wall
        int x = laser.getX();
        int y = laser.getY();

        x += laser.getDirection() == DirectionWrapper.RIGHT ? 1 : 0;
        x -= laser.getDirection() == DirectionWrapper.LEFT ? 1 : 0;
        y += laser.getDirection() == DirectionWrapper.DOWN ? 1 : 0;
        y -= laser.getDirection() == DirectionWrapper.UP ? 1 : 0;

        if (isWall(x, y)) {
            return;
        }

        LaserWrapper newLaser = new LaserWrapper(laser.getDirection(), x, y);
        tableOfEntities.get(x, y).add(newLaser);
        createLasers(newLaser);
    }

    public boolean isWall(int x, int y) {
       return tableOfEntities.get(x, y).stream().anyMatch(entity -> entity instanceof WallWrapper);
    }

    public void createMine(int x, int y) {
        // TODO we place the mine under ourselves so when does it explode? after the next move? maybe minimax already solves this by escaping?
        MineWrapper mine = new MineWrapper(null, x, y);
        mines.add(mine);
        tableOfEntities.get(x, y).add(mine);
    }

    public int countBulletsFlyingAt(TankWrapper tank){
        //check all 4 directions of the tank see if any bullets are flying at it
        int x = tank.getX();
        int y = tank.getY();
        int count = 0;
        for (DirectionWrapper direction : DirectionWrapper.values()) {
            int dx = direction == DirectionWrapper.RIGHT ? 1 : 0;
            dx -= direction == DirectionWrapper.LEFT ? 1 : 0;
            int dy = direction == DirectionWrapper.DOWN ? 1 : 0;
            dy -= direction == DirectionWrapper.UP ? 1 : 0;
            int newX = x + dx;
            int newY = y + dy;
            while (newX >= 0 && newX < tableOfEntities.getWidth() && newY >= 0 && newY < tableOfEntities.getHeight()) {
                if (tableOfEntities.get(newX, newY).stream().anyMatch(entity -> entity instanceof BulletWrapper && ((BulletWrapper) entity).getDirection()==direction)) {
                    count++;
                }
                newX += dx;
                newY += dy;
            }
        }
        return count;
    }

    public float evaluate() {
        float score = 0;
        //todo cratet Tank OurTank field
        return 0;
    }
}
