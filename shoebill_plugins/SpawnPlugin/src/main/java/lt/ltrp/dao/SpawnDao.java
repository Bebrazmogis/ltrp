package lt.ltrp.dao;

import lt.ltrp.spawn.data.SpawnData;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.22.
 */
public interface SpawnDao {


    SpawnData get(int userId);
    SpawnData get(LtrpPlayer player);

    void update(int userId, SpawnData spawnData);
    void update(LtrpPlayer player, SpawnData spawnData);


}
