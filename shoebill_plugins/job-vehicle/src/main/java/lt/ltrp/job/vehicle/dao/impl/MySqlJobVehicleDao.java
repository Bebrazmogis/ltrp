package lt.ltrp.job.vehicle.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.vehicle.dao.impl.AbstractMySqlVehicleDaoImpl;
import lt.ltrp.job.dao.JobVehicleDao;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.job.object.JobVehicle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.23.
 */
public class MySqlJobVehicleDao extends AbstractMySqlVehicleDaoImpl implements JobVehicleDao {


    public MySqlJobVehicleDao(DataSource ds, EventManager eventManager) {
        super(ds, eventManager);
    }

    @Override
    public Collection<JobVehicle> get(Job job) throws LoadingException {
        String sql = "SELECT vehicles.*, job_vehicles.* FROM job_vehicles " +
                "LEFT JOIN vehicles ON vehicles.id = job_vehicles.id " +
                "WHERE job_vehicles.job_id = ?";
        Collection<JobVehicle> vehicles = new ArrayList<>();
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, job.getUUID());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                int rankId = result.getInt("rank_id");
                JobRank rank = job.getRankByUUID(rankId);
                JobVehicle vehicle = JobVehicle.create(
                        result.getInt("id"),
                        job,
                        result.getInt("model"),
                        new AngledLocation(result.getFloat("x"),
                                result.getFloat("y"),
                                result.getFloat("z"),
                                result.getInt("interior"),
                                result.getInt("virtual_world"),
                                result.getFloat("angle")
                        ),
                        result.getInt("color1"),
                        result.getInt("color2"),
                        rank,
                        result.getString("license"),
                        result.getFloat("mileage")
                );
                vehicles.add(vehicle);
            }
        } catch(SQLException e) {
            throw new LoadingException("Could not job " + job.getUUID() + " vehicles", e);
        }
        return vehicles;
    }

    @Override
    public int insert(JobVehicle jobVehicle) {
        String jobVehicleSql = "INSERT INTO job_vehicles (id, job_id, rank_id) VALUES (?, ?, ?)";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement jobVehicleStmt = con.prepareStatement(jobVehicleSql, Statement.RETURN_GENERATED_KEYS);
        ) {
            super.insert(jobVehicle);
            jobVehicleStmt.setInt(1, jobVehicle.getUUID());
            jobVehicleStmt.setInt(2, jobVehicle.getJob().getUUID());
            jobVehicleStmt.setInt(3, jobVehicle.getRequiredRank().getNumber());
            jobVehicleStmt.execute();
            ResultSet r = jobVehicleStmt.getGeneratedKeys();
            if(r.next())
                return r.getInt(1);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void delete(JobVehicle jobVehicle) {
        super.delete(jobVehicle);
    }

    @Override
    public void update(JobVehicle jobVehicle) {
        String jobVehicleSql = "UPDATE job_vehicles SET job_id = ?, rank_id = ? WHERE id = ?";
        try (
                Connection con = getDataSource().getConnection();

                PreparedStatement jobVehicleStmt = con.prepareStatement(jobVehicleSql);
        ) {
            super.update(jobVehicle);
            jobVehicleStmt.setInt(1, jobVehicle.getJob().getUUID());
            jobVehicleStmt.setInt(2, jobVehicle.getRequiredRank().getNumber());
            jobVehicleStmt.setInt(3, jobVehicle.getUUID());
            jobVehicleStmt.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
