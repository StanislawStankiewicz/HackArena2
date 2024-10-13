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

/**
 * Abstract class representing a response from an agent.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public abstract class AgentResponse {

    /**
     * Converts the response to a PacketType.
     * 
     * @return the PacketType of the response.
     */
    @JsonProperty("type")
    public abstract PacketType toPacketType();

    /**
     * Payload of the response.
     */
    @JsonProperty("payload")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, Object> payload = new HashMap<>();

    /**
     * Class representing a movement response.
     */
    @JsonTypeName("movement")
    @Getter
    @Setter
    @ToString
    public static class Move extends AgentResponse {
        /**
         * Constructor for Move.
         * 
         * @param direction the direction of the movement.
         */
        public Move(MoveDirection direction) {
            this.payload.put("direction", direction);
        }

        /**
         * Converts the movement response to a PacketType.
         * 
         * @return the PacketType of the movement response.
         */
        @Override
        public PacketType toPacketType() {
            return PacketType.MOVEMENT;
        }
    }

    /**
     * Class representing a rotation response.
     */
    @JsonTypeName("rotation")
    @Getter
    @Setter
    @ToString
    public static class RotationResponse extends AgentResponse {

        /**
         * Constructor for RotationResponse.
         * 
         * @param tankRotationDirection   the direction of the tank rotation.
         * @param turretRotationDirection the direction of the turret rotation.
         */
        public RotationResponse(Optional<RotationDirection> tankRotationDirection,
                Optional<RotationDirection> turretRotationDirection) {
            this.payload.put("tankRotation", tankRotationDirection.orElse(null));
            this.payload.put("turretRotation", turretRotationDirection.orElse(null));
        }

        /**
         * Converts the rotation response to a PacketType.
         * 
         * @return the PacketType of the rotation response.
         */
        @Override
        public PacketType toPacketType() {
            return PacketType.ROTATION;
        }
    }

    /**
     * Class representing a pass response.
     */
    @JsonTypeName("pass")
    @ToString
    public static class ResponsePass extends AgentResponse {
        /**
         * Converts the pass response to a PacketType.
         * 
         * @return the PacketType of the pass response.
         */
        @Override
        public PacketType toPacketType() {
            return PacketType.PASS;
        }
    }

    /**
     * Class representing an ability use response.
     */
    @JsonTypeName("abilityUse")
    @Getter
    @Setter
    @ToString
    public static class AbilityUse extends AgentResponse {
        /**
         * Constructor for AbilityUse.
         * 
         * @param abilityType the type of the ability.
         */
        public AbilityUse(AbilityType abilityType) {
            this.payload.put("abilityType", abilityType);
        }

        /**
         * Converts the ability use response to a PacketType.
         * 
         * @return the PacketType of the ability use response.
         */
        @Override
        public PacketType toPacketType() {
            return PacketType.ABILITY_USE;
        }
    }

    /**
     * Creates a movement response.
     * 
     * @param direction the direction of the movement.
     * @return a new Move instance.
     */
    public static AgentResponse createMoveResponse(MoveDirection direction) {
        return new Move(direction);
    }

    /**
     * Creates a rotation response.
     * 
     * @param tankRotationDirection   the direction of the tank rotation.
     * @param turretRotationDirection the direction of the turret rotation.
     * @return a new RotationResponse instance.
     */
    public static AgentResponse createRotationResponse(Optional<RotationDirection> tankRotationDirection,
            Optional<RotationDirection> turretRotationDirection) {
        if (tankRotationDirection.isEmpty() && turretRotationDirection.isEmpty()) {
            return new ResponsePass();
        }
        return new RotationResponse(tankRotationDirection, turretRotationDirection);
    }

    /**
     * Creates a pass response.
     * 
     * @return a new ResponsePass instance.
     */
    public static AgentResponse createPassResponse() {
        return new ResponsePass();
    }

    /**
     * Creates an ability use response.
     * 
     * @param abilityType the type of the ability.
     * @return a new AbilityUse instance.
     */
    public static AgentResponse createAbilityUseResponse(AbilityType abilityType) {
        return new AbilityUse(abilityType);
    }
}
