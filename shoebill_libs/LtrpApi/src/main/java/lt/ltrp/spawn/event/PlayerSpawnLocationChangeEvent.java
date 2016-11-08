package lt.ltrp.spawn.event;

import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.spawn.data.SpawnData;

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
