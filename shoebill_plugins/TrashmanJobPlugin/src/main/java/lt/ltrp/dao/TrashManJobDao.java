package lt.ltrp.dao;

import lt.ltrp.data.TrashMissions;
import lt.ltrp.object.TrashManJob;

/**
 * @author Bebras
 *         2016.05.24.
 */
public interface TrashManJobDao extends JobDao {

    TrashManJob get(int id);
    TrashMissions getMissions();

    //void update(TrashMission mission);

}
