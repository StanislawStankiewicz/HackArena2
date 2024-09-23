package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Optional;

@Data
@NoArgsConstructor
public  class Bullet extends Tile.TilePayload {
    @JsonProperty("direction")
    private Direction direction;

    @JsonProperty("id")
    private long id;

    @JsonProperty("speed")
    private double speed;

    @JsonProperty("score")
    private Optional<Double> score;
}