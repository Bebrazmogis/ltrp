package lt.ltrp.spawn.event;

import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.spawn.data.SpawnData;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class PlayerRequestSpawnEvent extends PlayerEvent {

    private SpawnData spawnData;

    public PlayerRequestSpawnEvent(LtrpPlayer player, SpawnData spawnData) {
        super(player);
        this.spawnData = spawnData;
    }

    public SpawnData getSpawnData() {
        return spawnData;
    }
}
