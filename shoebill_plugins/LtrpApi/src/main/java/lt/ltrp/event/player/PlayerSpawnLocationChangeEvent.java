package lt.ltrp.event.player;

import lt.ltrp.data.SpawnData;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class PlayerSpawnLocationChangeEvent extends PlayerEvent {

    private SpawnData spawnData;

    public PlayerSpawnLocationChangeEvent(LtrpPlayer player, SpawnData spawnData) {
        super(player);
        this.spawnData = spawnData;
    }

    public SpawnData getSpawnData() {
        return spawnData;
    }
}
