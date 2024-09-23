package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Turret {
    @JsonProperty("bulletCount")
    private Optional<Long> bulletCount;

    @JsonProperty("ticksToRegenBullet")
    private Optional<Double> ticksToRegenBullet;

    private Direction direction;
}