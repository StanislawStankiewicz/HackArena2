package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.INIT_SGGW.MonoTanksClient.utils.OptionalDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("color")
    private long color;

    @JsonDeserialize(using = OptionalDeserializer.class)
    @JsonProperty("ping")
    private Optional<Long> ping;

    @JsonDeserialize(using = OptionalDeserializer.class)
    @JsonProperty("score")
    private Optional<Long> score;

    @JsonDeserialize(using = OptionalDeserializer.class)
    @JsonProperty("ticksToRegen")
    private Optional<Long> ticksToRegen;
}