package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.INIT_SGGW.MonoTanksClient.utils.CamelCaseEnumDeserializer;
import com.github.INIT_SGGW.MonoTanksClient.utils.CamelCaseEnumSerializer;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.ItemType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.List;

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
     * The list of objects on this tile, which can be of various types.
     */
    private final List<TilePayload> objects;

    /**
     * Abstract base class for different types of tile payloads.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Wall.class, name = "wall"),
            @JsonSubTypes.Type(value = Tank.class, name = "tank"),
            @JsonSubTypes.Type(value = Bullet.class, name = "bullet"),
            @JsonSubTypes.Type(value = Item.class, name = "item"),
            @JsonSubTypes.Type(value = Laser.class, name = "laser"),
            @JsonSubTypes.Type(value = Mine.class, name = "mine")
    })
    public static abstract class TilePayload {
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

        /**
         * The secondary item associated with the tank.
         * This is only present for the player's own tank and when they have a secondary
         * item.
         */
        private Optional<Integer> secondaryItem;
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

        /**
         * The type of the bullet.
         */
        private BulletType type;
    }

    /**
     * Enum representing the possible types of bullets.
     */
    @JsonSerialize(using = CamelCaseEnumSerializer.class)
    @JsonDeserialize(using = CamelCaseEnumDeserializer.class)
    public enum BulletType {
        BASIC,
        DOUBLE
    }

    /**
     * Represents an item tile payload.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Item extends Tile.TilePayload {
        /**
         * The type of the item.
         */
        private ItemType itemType;
    }

    /**
     * Represents a laser tile payload.
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Laser extends Tile.TilePayload {
        /**
         * The unique identifier for the laser.
         */
        private long id;

        /**
         * The orientation of the laser.
         */
        private LaserDirection orientation;
    }

    /**
     * Represents a mine tile payload.
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Mine extends Tile.TilePayload {
        /**
         * The unique identifier for the mine.
         */
        private long id;

        /**
         * The number of remaining ticks before the mine explodes.
         */
        private Integer explosionRemainingTicks;
    }

    /**
     * Enum representing the possible orientations of a laser.
     */
    @RequiredArgsConstructor
    @JsonSerialize(using = CamelCaseEnumSerializer.class)
    @JsonDeserialize(using = CamelCaseEnumDeserializer.class)
    public enum LaserDirection {
        /**
         * Represents a horizontal laser orientation.
         */
        HORIZONTAL,

        /**
         * Represents a vertical laser orientation.
         */
        VERTICAL;
    }
}
