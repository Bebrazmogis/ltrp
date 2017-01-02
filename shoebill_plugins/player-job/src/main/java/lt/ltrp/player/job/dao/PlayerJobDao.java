package lt.ltrp.player.job.dao;

import lt.ltrp.object.PlayerData;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.24.
 */
public interface PlayerJobDao {

    void update(PlayerJobData jobData);
    void remove(PlayerJobData jobData);
    void insert(PlayerJobData jobData);
    PlayerJobData get(PlayerData player);

}
