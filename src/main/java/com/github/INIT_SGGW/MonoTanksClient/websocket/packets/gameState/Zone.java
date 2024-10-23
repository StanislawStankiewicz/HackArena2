package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a zone in the game state.
 */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class Zone {
    /**
     * The index of the zone.
     */
    private int index;

    /**
     * The x-coordinate of the left side of the zone.
     */
    private long x;

    /**
     * The y-coordinate of the top side of the zone.
     */
    private long y;

    /**
     * The width of the zone.
     */
    private long width;

    /**
     * The height of the zone.
     */
    private long height;

    /**
     * The status of the zone.
     */
    private ZoneStatus status;

    /**
     * Abstract base class for different types of zone statuses.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ZoneStatus.Neutral.class, name = "neutral"),
            @JsonSubTypes.Type(value = ZoneStatus.BeingCaptured.class, name = "beingCaptured"),
            @JsonSubTypes.Type(value = ZoneStatus.Captured.class, name = "captured"),
            @JsonSubTypes.Type(value = ZoneStatus.BeingContested.class, name = "beingContested"),
            @JsonSubTypes.Type(value = ZoneStatus.BeingRetaken.class, name = "beingRetaken")
    })
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static abstract class ZoneStatus {
        /**
         * Represents a neutral zone status.
         */
        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
        public static class Neutral extends ZoneStatus {
        }

        /**
         * Represents a zone status where the zone is being captured.
         */
        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
        public static class BeingCaptured extends ZoneStatus {
            /**
             * The remaining ticks for the capture process.
             */
            private long remainingTicks;

            /**
             * The ID of the player capturing the zone.
             */
            private String playerId;
        }

        /**
         * Represents a captured zone status.
         */
        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
        public static class Captured extends ZoneStatus {
            /**
             * The ID of the player who captured the zone.
             */
            private String playerId;
        }

        /**
         * Represents a zone status where the zone is being contested.
         */
        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
        public static class BeingContested extends ZoneStatus {
            /**
             * The ID of the player who captured the zone.
             */
            private Optional<String> capturedById;
        }

        /**
         * Represents a zone status where the zone is being retaken.
         */
        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
        public static class BeingRetaken extends ZoneStatus {
            /**
             * The remaining ticks for the retake process.
             */
            private long remainingTicks;

            /**
             * The ID of the player who captured the zone.
             */
            private String capturedById;

            /**
             * The ID of the player retaking the zone.
             */
            private String retakenById;
        }
    }
}