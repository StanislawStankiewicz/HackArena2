package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Optional;

/**
 * Represents a tile in the game state.
 */
@Data
@JsonDeserialize
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Tile {

    /**
     * Indicates whether the tile is visible.
     */
    private final boolean visible;

    /**
     * The index of the zone this tile belongs to, if any.
     */
    private final Optional<Integer> zoneIndex;

    /**
     * The payload of the tile, which can be of various types.
     */
    private final TilePayload payload;

    /**
     * Abstract base class for different types of tile payloads.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Empty.class, name = "empty"),
            @JsonSubTypes.Type(value = Wall.class, name = "wall"),
            @JsonSubTypes.Type(value = Tank.class, name = "tank"),
            @JsonSubTypes.Type(value = Bullet.class, name = "bullet")
    })
    public static abstract class TilePayload {
    }

    /**
     * Represents an empty tile payload.
     */
    @Data
    @NoArgsConstructor
    public static class Empty extends TilePayload {
    }

    /**
     * Represents a wall tile payload.
     */
    @Data
    @NoArgsConstructor
    public static class Wall extends TilePayload {
    }

    /**
     * Represents a tank tile payload.
     */
    @Data
    @NoArgsConstructor
    @JsonDeserialize
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Tank extends Tile.TilePayload {
        /**
         * The direction the tank is facing.
         */
        private Direction direction;

        /**
         * The health of the tank, if available.
         */
        private Optional<Long> health;

        /**
         * The ID of the owner of the tank.
         */
        private String ownerId;

        /**
         * The turret associated with the tank.
         */
        private Turret turret;
    }

    /**
     * Represents a bullet tile payload.
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Bullet extends Tile.TilePayload {
        /**
         * The direction the bullet is traveling.
         */
        private Direction direction;

        /**
         * The unique identifier for the bullet.
         */
        private long id;

        /**
         * The speed of the bullet.
         * The speed is measured in tiles per tick.
         */
        private double speed;
    }
}