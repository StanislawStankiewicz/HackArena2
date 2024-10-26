package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.List;

public class Utility {
    public static void printMap(GameState gameState, String myId) {
        // Print map as ascii
        System.out.println("Map:");
        for (Tile[] row : gameState.map().tiles()) {
            for (Tile tile : row) {
                List<Tile.TileEntity> entities = tile.getEntities();
                String symbol = " ";

                if (tile.isVisible()) {
                    symbol = ".";
                }

                if (tile.getZoneIndex().isPresent()) {
                    int zoneIndex = tile.getZoneIndex().get();
                    if (tile.isVisible()) {
                        symbol = String.valueOf((char) zoneIndex);
                    } else {
                        symbol = String.valueOf((char) (zoneIndex + 32));
                    }
                }

                for (Tile.TileEntity entity : entities) {
                    if (entity instanceof Tile.Tank tank) {
                        if (tank.getOwnerId().equals(myId)) {
                            if (tank.getDirection() == Direction.UP) {
                                symbol = "^";
                            } else if (tank.getDirection() == Direction.DOWN) {
                                symbol = "v";
                            } else if (tank.getDirection() == Direction.LEFT) {
                                symbol = "<";
                            } else if (tank.getDirection() == Direction.RIGHT) {
                                symbol = ">";
                            }

                            // There is also turret direction
                            // tank.getTurret().direction()

                        } else {
                            symbol = "T";
                        }
                    } else if (entity instanceof Tile.Wall) {
                        symbol = "#";
                    } else if (entity instanceof Tile.Bullet bullet) {
                        if (bullet.getDirection() == Direction.UP) {
                            symbol = "↑";
                        } else if (bullet.getDirection() == Direction.DOWN) {
                            symbol = "↓";
                        } else if (bullet.getDirection() == Direction.LEFT) {
                            symbol = "←";
                        } else if (bullet.getDirection() == Direction.RIGHT) {
                            symbol = "→";
                        }
                    } else if (entity instanceof Tile.Laser laser) {
                        if (laser.getOrientation() == Tile.LaserDirection.VERTICAL) {
                            symbol = "|";
                        } else if (laser.getOrientation() == Tile.LaserDirection.HORIZONTAL) {
                            symbol = "-";
                        }
                    } else if (entity instanceof Tile.Mine) {
                        symbol = "X";
                    } else if (entity instanceof Tile.Item item) {
                        if (item.getItemType() == ItemType.DOUBLE_BULLET) {
                            symbol = "D";
                        } else if (item.getItemType() == ItemType.LASER) {
                            symbol = "L";
                        } else if (item.getItemType() == ItemType.MINE) {
                            symbol = "M";
                        } else if (item.getItemType() == ItemType.RADAR) {
                            symbol = "R";
                        }
                    }
                }

                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }
}
