package lt.ltrp.business.dao.impl;

import lt.ltrp.dao.TrashManJobDao;
import lt.ltrp.data.TrashMission;
import lt.ltrp.data.TrashMissions;
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
public class MySqlTrashManJobImpl implements TrashManJobDao {

    private DataSource dataSource;
    private EventManager eventManager;

    public MySqlTrashManJobImpl(DataSource dataSource, EventManager eventManager) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
    }

    @Override
    public TrashMissions getMissions() {
        TrashMissions trashMissions = new TrashMissions();
        String sql = "SELECT garbageman_missions.*, garbageman_mission_garbage.* FROM garbageman_missions LEFT JOIN garbageman_mission_garbage ON garbageman_missions.id = garbageman_mission_garbage.mission_id";
        try (
                Connection connection = dataSource.getConnection();
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

}
