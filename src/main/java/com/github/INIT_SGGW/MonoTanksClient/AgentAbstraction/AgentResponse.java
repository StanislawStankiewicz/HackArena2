package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.github.INIT_SGGW.MonoTanksClient.websocket.PacketType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public abstract class AgentResponse {

    @JsonProperty("type")
    public abstract PacketType toPacketType();

    @JsonTypeName("tankMovement")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Move extends AgentResponse {
        @JsonProperty("payload")
        private MovePayload payload;

        public Move(MoveDirection direction) {
            this.payload = new MovePayload(direction);
        }

        @Override
        public PacketType toPacketType() {
            return PacketType.TANK_MOVEMENT;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        public static class MovePayload {
            private MoveDirection direction;
        }
    }

    @JsonTypeName("tankRotation")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class RotationResponse extends AgentResponse {
        @JsonProperty("payload")
        private RotationPayload payload;

        public RotationResponse(Optional<RotationDirection> tankRotationDirection, Optional<RotationDirection> turretRotationDirection) {
            this.payload = new RotationPayload(tankRotationDirection, turretRotationDirection);
        }

        @Override
        public PacketType toPacketType() {
            return PacketType.TANK_ROTATION;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        public static class RotationPayload {
            @JsonProperty("tankRotation")
            @JsonInclude(JsonInclude.Include.NON_ABSENT)
            private Optional<RotationDirection> tankRotationDirection;

            @JsonProperty("turretRotation")
            @JsonInclude(JsonInclude.Include.NON_ABSENT)
            private Optional<RotationDirection> turretRotationDirection;
        }
    }

    @JsonTypeName("tankShoot")
    @ToString
    public static class Shoot extends AgentResponse {
        @JsonProperty("payload")
        private final Map<String, Object> payload = Collections.emptyMap();

        @Override
        public PacketType toPacketType() {
            return PacketType.TANK_SHOOT;
        }
    }

    public static AgentResponse createMoveResponse(MoveDirection direction) {
        return new Move(direction);
    }

    public static AgentResponse createRotationResponse(Optional<RotationDirection> tankRotationDirection, Optional<RotationDirection> turretRotationDirection) {
        return new RotationResponse(tankRotationDirection, turretRotationDirection);
    }

    public static AgentResponse createShootResponse() {
        return new Shoot();
    }
}