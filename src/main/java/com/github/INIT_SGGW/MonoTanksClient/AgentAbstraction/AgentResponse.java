package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.github.INIT_SGGW.MonoTanksClient.websocket.PacketType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public abstract class AgentResponse {

    @JsonProperty("type")
    public abstract PacketType toPacketType();

    @JsonProperty("payload")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, Object> payload = new HashMap<>();


    @JsonTypeName("tankMovement")
    @Getter
    @Setter
    @ToString
    public static class Move extends AgentResponse {
        public Move(MoveDirection direction) {
            this.payload.put("direction", direction);
        }

        @Override
        public PacketType toPacketType() {
            return PacketType.TANK_MOVEMENT;
        }
    }

    @JsonTypeName("tankRotation")
    @Getter
    @Setter
    @ToString
    public static class RotationResponse extends AgentResponse {

        public RotationResponse(Optional<RotationDirection> tankRotationDirection, Optional<RotationDirection> turretRotationDirection) {
            this.payload.put("tankRotation", tankRotationDirection.orElse(null));
            this.payload.put("turretRotation", turretRotationDirection.orElse(null));
        }

        @Override
        public PacketType toPacketType() {
            return PacketType.TANK_ROTATION;
        }
}

    @JsonTypeName("tankShoot")
    @ToString
    public static class Shoot extends AgentResponse {
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