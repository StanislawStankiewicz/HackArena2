package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Direction;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile.Bullet;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile.Tank;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Turret;

import java.io.IOException;
import java.util.Optional;

public class MapDeserializer extends JsonDeserializer<Tile[][]> {
    @Override
    public Tile[][] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode rootNode = mapper.readTree(p);
        JsonNode visibilityNode = rootNode.get("visibility");

        if (visibilityNode == null || !visibilityNode.isArray()) {
            return new Tile[0][0]; // Return an empty map if visibility is not present or not an array
        }

        JsonNode rowsNode = visibilityNode;
        JsonNode colsNode = visibilityNode.get(0);
        int rows = rowsNode.size();
        int cols = colsNode.asText().length();

        Tile[][] map = new Tile[rows][cols];

        // Deserialize visibility
        Boolean[][] visibility = new Boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            JsonNode rowNode = visibilityNode.get(i);
            for (int j = 0; j < cols; j++) {
                visibility[i][j] = rowNode.asText().charAt(j) == '1';
            }
        }

        // Deserialize zones
        Integer[][] zones = new Integer[rows][cols];
        JsonNode zonesNode = rootNode.get("zones");

        if (zonesNode == null || !zonesNode.isArray()) {
            return new Tile[0][0]; // Return an empty map if zones is not present or not an array
        }

        for (JsonNode zoneNode : zonesNode) {
            int x = zoneNode.get("x").asInt();
            int y = zoneNode.get("y").asInt();
            int width = zoneNode.get("width").asInt();
            int height = zoneNode.get("height").asInt();
            int index = zoneNode.get("index").asInt();

            for (int i = y; i < y + height; i++) {
                for (int j = x; j < x + width; j++) {
                    zones[i][j] = index;
                }
            }
        }

        // Deserialize tiles
        JsonNode tilesNode = rootNode.get("tiles");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JsonNode rowNode = tilesNode.get(j);
                JsonNode tileNode = rowNode.get(i);

                boolean isVisible = visibility[i][j];
                Optional<Integer> zoneIndex = zones[i][j] == null ? Optional.empty() : Optional.of(zones[i][j]);

                if (!tileNode.isArray()) {
                    System.err.println("Tile at [" + i + "][" + j + "] is not an array");
                    map[i][j] = new Tile(isVisible, zoneIndex, new Tile.Empty());
                    continue;
                }

                // Deserialize the tile payload
                Tile.TilePayload payload = new Tile.Empty();
                if (tileNode.size() > 0) {
                    JsonNode payloadNode = tileNode.get(0);
                    String type = payloadNode.get("type").asText();
                    switch (type) {
                        case "wall":
                            payload = new Tile.Wall();
                            break;

                        case "tank":
                            Tank tank = new Tank();
                            tank.setOwnerId(payloadNode.get("payload").get("ownerId").asText());

                            if (payloadNode.get("payload").get("health") != null) {
                                Long health = payloadNode.get("payload").get("health").asLong();
                                tank.setHealth(Optional.of(health));
                            } else {
                                tank.setHealth(Optional.empty());
                            }

                            int directionInt = payloadNode.get("payload").get("direction").asInt();
                            Direction direction = Direction.fromValue(directionInt);
                            tank.setDirection(direction);

                            JsonNode turretNode = payloadNode.get("payload").get("turret");
                            int turretDirectionInt = turretNode.get("direction").asInt();
                            Direction turretDirection = Direction.fromValue(turretDirectionInt);

                            Optional<Long> bulletCount = Optional.empty();
                            if (turretNode.get("bulletCount") != null) {
                                bulletCount = Optional.of(turretNode.get("bulletCount").asLong());
                            }

                            Optional<Double> ticksToRegenBullet = Optional.empty();
                            if (turretNode.get("ticksToRegenBullet") != null) {
                                ticksToRegenBullet = Optional.of(turretNode.get("ticksToRegenBullet").asDouble());
                            }

                            Turret turret = new Turret(turretDirection, bulletCount, ticksToRegenBullet);
                            tank.setTurret(turret);
                            payload = tank;
                            break;

                        case "bullet":
                            Bullet bullet = new Bullet();
                            int bulletDirectionInt = payloadNode.get("payload").get("direction").asInt();
                            Direction bulletDirection = Direction.fromValue(bulletDirectionInt);
                            bullet.setDirection(bulletDirection);
                            payload = bullet;
                            break;
                    }
                }

                map[i][j] = new Tile(isVisible, zoneIndex, payload);
            }
        }

        return map;
    }
}