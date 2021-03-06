package lt.ltrp.event;

import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.data.SpawnData;

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
