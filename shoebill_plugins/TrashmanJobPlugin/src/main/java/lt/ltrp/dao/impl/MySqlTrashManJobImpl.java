package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.dao.TrashManJobDao;
import lt.ltrp.data.TrashMission;
import lt.ltrp.data.TrashMissions;
import lt.ltrp.object.TrashManJob;
import lt.ltrp.object.impl.TrashManJobImpl;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlTrashManJobImpl extends MySqlJobDaoImpl implements TrashManJobDao {


    public MySqlTrashManJobImpl(DataSource dataSource, JobVehicleDao jobVehicleDao, EventManager eventManager) {
        super(dataSource, jobVehicleDao, eventManager);
    }

    @Override
    public TrashMissions getMissions() {
        TrashMissions trashMissions = new TrashMissions();
        String sql = "SELECT garbageman_missions.*, garbageman_mission_garbage.* FROM garbageman_missions LEFT JOIN garbageman_mission_garbage ON garbageman_missions.id = garbageman_mission_garbage.mission_id";
        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            ResultSet result = stmt.executeQuery();

            TrashMission mission = null;
            while(result.next()) {
                int id = result.getInt("id");
                if (mission == null || mission.getId() != id) {
                    if (mission != null)
                        trashMissions.add(mission);
                    mission = new TrashMission(id, result.getString("name"));
                }
                DynamicObject ob = DynamicObject.create(result.getInt("model_id"), new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z")), new Vector3D());
                mission.addGarbage(ob);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trashMissions;
    }

    @Override
    public TrashManJob get(int i) {
        TrashManJobImpl job = null;
        if(isValid(i)) {
            job = new TrashManJobImpl(i, getEventManager());
            try {
                super.load(job);
            } catch (LoadingException e) {
                e.printStackTrace();
            }
            job.setMissions(getMissions());
        }
        return job;
    }
}
