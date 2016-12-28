package lt.ltrp.business.dao.impl;

import lt.ltrp.dao.EntranceDao;
import lt.ltrp.data.Color;
import lt.ltrp.job.JobController;
import lt.ltrp.object.Entrance;
import lt.ltrp.job.object.Job;
import lt.ltrp.object.impl.EntranceImpl;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class MySqlEntranceDaoImpl implements EntranceDao {

    private DataSource dataSource;
    private EventManager eventManager;

    public MySqlEntranceDaoImpl(DataSource dataSource, EventManager eventManager) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
    }

    @Override
    public List<Entrance> get() {
        List<Entrance> entrances= new ArrayList<>();
        String sql = "SELECT * FROM entrances";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next())
                entrances.add(resultToEntrance(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entrances;
    }

    @Override
    public Entrance get(int id) {
        String sql = "SELECT * FROM entrances WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next())
                return resultToEntrance(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Entrance entrance) {
        String sql = "UPDATE entrances SET name = ?, x = ?, y = ?, z = ?, angle = ?, interior = ?, virtual_world = ?, label_color = ?, pickup_model = ?, job_id = ?, is_vehicle = ?, exit_id = ? WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, entrance.getText());
            stmt.setFloat(2, entrance.getLocation().getX());
            stmt.setFloat(3, entrance.getLocation().getY());
            stmt.setFloat(4, entrance.getLocation().getZ());
            stmt.setFloat(5, entrance.getLocation().getAngle());
            stmt.setInt(6, entrance.getLocation().getInteriorId());
            stmt.setInt(7, entrance.getLocation().getWorldId());
            stmt.setInt(8, entrance.getColor().getValue());
            stmt.setInt(9, entrance.getPickupModelId());
            if(entrance.isJob())
                stmt.setInt(10, entrance.getJob().getUUID());
            else
                stmt.setNull(10, Types.INTEGER);
            stmt.setBoolean(11, entrance.allowsVehicles());
            if(entrance.getExit() != null)
                stmt.setInt(12, entrance.getExit().getUUID());
            else
                stmt.setNull(12, Types.INTEGER);
            stmt.setInt(13, entrance.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Entrance entrance) {
        String sql = "DELETE FROM entrances WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
             stmt.setInt(1, entrance.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Entrance entrance) {
        String sql = "INSERT INTO entrances (name, x, y, z, angle, interior, virtual_world, label_color, pickup_model, job_id, is_vehicle, exit_id) VALUES (?, ?, ?, ?, ?, ? ,? ,? ,?, ?, ?, ?)";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, entrance.getText());
            stmt.setFloat(2, entrance.getLocation().getX());
            stmt.setFloat(3, entrance.getLocation().getY());
            stmt.setFloat(4, entrance.getLocation().getZ());
            stmt.setFloat(5, entrance.getLocation().getAngle());
            stmt.setInt(6, entrance.getLocation().getInteriorId());
            stmt.setInt(7, entrance.getLocation().getWorldId());
            stmt.setInt(8, entrance.getColor().getValue());
            stmt.setInt(9, entrance.getPickupModelId());
            if(entrance.isJob())
                stmt.setInt(10, entrance.getJob().getUUID());
            else
                stmt.setNull(10, Types.INTEGER);
            stmt.setBoolean(11, entrance.allowsVehicles());
            if(entrance.getExit() != null)
                stmt.setInt(12, entrance.getExit().getUUID());
            else
                stmt.setNull(12, Types.INTEGER);
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                entrance.setUUID(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private EntranceImpl resultToEntrance(ResultSet r) throws SQLException {
        EntranceImpl en;
        int jobId = r.getInt("job_id");
        Job job = null;
        if(!r.wasNull())
            job = JobController.Companion.getInstance().get(jobId);
        en = new EntranceImpl(r.getInt("id"), new Color(r.getInt("label_color")),
                r.getInt("pickup_model"),
                r.getString("name"),
                new AngledLocation(r.getFloat("x"), r.getFloat("y"), r.getFloat("z"), r.getInt("interior"), r.getInt("virtual_world"), r.getFloat("angle")),
                job,
                r.getBoolean("is_vehicle"),
                eventManager);
        int enId = r.getInt("exit_id");
        if(!r.wasNull()) {
            Entrance ex = Entrance.get(enId);
            if(ex != null)
                en.setExit(ex);
            else
                en.setExit(get(enId));
        }
        return en;
    }
}
