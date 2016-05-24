package lt.ltrp.dao;

import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.Job;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Rank;

import java.util.Map;

/**
 * @author Bebras
 *         2016.05.24.
 */
public interface PlayerJobDao {

    void update(PlayerJobData jobData);
    void remove(PlayerJobData jobData);
    void insert(PlayerJobData jobData);
    PlayerJobData get(LtrpPlayer player);
    Map<String, Rank> getEmployeeList(Job job);

}
