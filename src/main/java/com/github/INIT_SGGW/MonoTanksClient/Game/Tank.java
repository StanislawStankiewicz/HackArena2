package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Optional;

@Data
@NoArgsConstructor
@JsonDeserialize
public class Tank extends Tile.TilePayload {
    @JsonProperty("direction")
    private Direction direction;

    @JsonProperty("health")
    private Optional<Long> health;

    @JsonProperty("ownerId")
    private String ownerId;

    @JsonProperty("turret")
    private Turret turret;
}