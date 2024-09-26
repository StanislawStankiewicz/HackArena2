package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@JsonDeserialize
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Tile {
    private final boolean visible;
    private final Optional<Integer> zoneIndex;
    private final TilePayload payload;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Empty.class, name = "empty"),
            @JsonSubTypes.Type(value = Wall.class, name = "wall"),
            @JsonSubTypes.Type(value = Tank.class, name = "tank"),
            @JsonSubTypes.Type(value = Bullet.class, name = "bullet")
    })
    public static abstract class TilePayload {
    }

    @Data
    @NoArgsConstructor
    public static class Empty extends TilePayload {
    }

    @Data
    @NoArgsConstructor
    public static class Wall extends TilePayload {
    }
}