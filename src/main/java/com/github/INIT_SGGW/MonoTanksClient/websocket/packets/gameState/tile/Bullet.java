package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bullet extends Tile.TilePayload {
    @JsonProperty("direction")
    private Direction direction;

    @JsonProperty("id")
    private long id;

    @JsonProperty("speed")
    private double speed;
}