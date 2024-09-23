package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Zone {
    private int index;
    private long x;
    private long y;
    private long width;
    private long height;
    private ZoneStatus status;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ZoneStatus.Neutral.class, name = "neutral"),
            @JsonSubTypes.Type(value = ZoneStatus.BeingCaptured.class, name = "beingCaptured"),
            @JsonSubTypes.Type(value = ZoneStatus.Captured.class, name = "captured"),
            @JsonSubTypes.Type(value = ZoneStatus.BeingContested.class, name = "beingContested"),
            @JsonSubTypes.Type(value = ZoneStatus.BeingRetaken.class, name = "beingRetaken")
    })
    public static abstract class ZoneStatus {
        @Data
        @NoArgsConstructor
        public static class Neutral extends ZoneStatus {}

        @Data
        @NoArgsConstructor
        public static class BeingCaptured extends ZoneStatus {
            private long remainingTicks;
            private String playerId;
        }

        @Data
        @NoArgsConstructor
        public static class Captured extends ZoneStatus {
            private String playerId;
        }

        @Data
        @NoArgsConstructor
        public static class BeingContested extends ZoneStatus {
            private String capturedById;
        }

        @Data
        @NoArgsConstructor
        public static class BeingRetaken extends ZoneStatus {
            private long remainingTicks;
            private String capturedById;
            private String retakenById;
        }
    }
}