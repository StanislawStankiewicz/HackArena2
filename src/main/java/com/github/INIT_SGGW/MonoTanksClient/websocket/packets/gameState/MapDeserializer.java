package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.*;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile.*;

import java.io.IOException;
import java.util.*;

public class MapDeserializer extends JsonDeserializer<Tile[][]> {
    private ObjectMapper mapper;

    @Override
    public Tile[][] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        mapper = (ObjectMapper) p.getCodec();
        JsonNode rootNode = mapper.readTree(p);

        Boolean[][] visibility = deserializeVisibility(rootNode);
        Integer[][] zones = deserializeZones(rootNode);
        int rows = visibility.length;
        int cols = visibility[0].length;

        Tile[][] map = new Tile[rows][cols];
        JsonNode tilesNode = rootNode.get("tiles");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map[i][j] = deserializeTile(tilesNode.get(j).get(i), visibility[i][j], zones[i][j]);
            }
        }

        return map;
    }

    private Boolean[][] deserializeVisibility(JsonNode rootNode) {
        JsonNode visibilityNode = rootNode.get("visibility");
        if (visibilityNode == null || !visibilityNode.isArray()) {
            return new Boolean[0][0];
        }

        int rows = visibilityNode.size();
        int cols = visibilityNode.get(0).asText().length();
        Boolean[][] visibility = new Boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            String rowString = visibilityNode.get(i).asText();
            for (int j = 0; j < cols; j++) {
                visibility[i][j] = rowString.charAt(j) == '1';
            }
        }

        return visibility;
    }

    private Integer[][] deserializeZones(JsonNode rootNode) {
        JsonNode zonesNode = rootNode.get("zones");
        if (zonesNode == null || !zonesNode.isArray()) {
            return new Integer[0][0];
        }

        int rows = rootNode.get("visibility").size();
        int cols = rootNode.get("visibility").get(0).asText().length();
        Integer[][] zones = new Integer[rows][cols];

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

        return zones;
    }

    private Tile deserializeTile(JsonNode tileNode, boolean isVisible, Integer zoneIndex) {
        Optional<Integer> zone = Optional.ofNullable(zoneIndex);
        List<Tile.TilePayload> payload = new ArrayList<>();

        if (tileNode.isArray() && tileNode.size() > 0) {
            JsonNode payloadNode = tileNode.get(0);
            String type = payloadNode.get("type").asText();
            payload.add(deserializePayload(type, payloadNode));
        }

        return new Tile(isVisible, zone, payload);
    }

    private Tile.TilePayload deserializePayload(String type, JsonNode payloadNode) {
        switch (type) {
            case "wall":
                return new Tile.Wall();
            case "tank":
                return deserializeTank(payloadNode);
            case "bullet":
                return deserializeBullet(payloadNode);
            case "item":
                return deserializeItem(payloadNode);
            case "laser":
                return deserializeLaser(payloadNode);
            case "mine":
                return deserializeMine(payloadNode);
            default:
                throw new IllegalArgumentException("Unknown tile type: " + type);
        }
    }

    private Tank deserializeTank(JsonNode payloadNode) {
        Tank tank = new Tank();
        JsonNode tankPayload = payloadNode.get("payload");
        tank.setOwnerId(tankPayload.get("ownerId").asText());
        tank.setHealth(Optional.ofNullable(tankPayload.get("health")).map(JsonNode::asLong));
        tank.setDirection(mapper.convertValue(tankPayload.get("direction"), Direction.class));
        tank.setTurret(deserializeTurret(tankPayload.get("turret")));
        return tank;
    }

    private Turret deserializeTurret(JsonNode turretNode) {
        Direction direction = mapper.convertValue(turretNode.get("direction"), Direction.class);
        Optional<Long> bulletCount = Optional.ofNullable(turretNode.get("bulletCount")).map(JsonNode::asLong);
        Optional<Double> ticksToRegenBullet = Optional.ofNullable(turretNode.get("ticksToRegenBullet"))
                .map(JsonNode::asDouble);
        return new Turret(direction, bulletCount, ticksToRegenBullet);
    }

    private Bullet deserializeBullet(JsonNode payloadNode) {
        Bullet bullet = new Bullet();
        bullet.setDirection(mapper.convertValue(payloadNode.get("payload").get("direction"), Direction.class));
        return bullet;
    }

    private Item deserializeItem(JsonNode payloadNode) {
        ItemType itemType = mapper.convertValue(payloadNode.get("payload").get("type"), ItemType.class);
        return new Item(itemType);
    }

    private Laser deserializeLaser(JsonNode payloadNode) {
        Laser laser = new Laser();
        JsonNode laserPayload = payloadNode.get("payload");
        laser.setId(laserPayload.get("id").asLong());
        laser.setOrientation(mapper.convertValue(laserPayload.get("orientation"), LaserDirection.class));
        return laser;
    }

    private Mine deserializeMine(JsonNode payloadNode) {
        Mine mine = new Mine();
        mine.setId(payloadNode.get("payload").get("id").asLong());
        return mine;
    }
}
