package lt.ltrp.dao.impl;


import lt.ltrp.LoadingException;
import lt.ltrp.dao.JobDao;
import lt.ltrp.dao.VehicleDao;
import lt.ltrp.job.*;
import lt.ltrp.job.drugdealer.DrugDealerJob;
import lt.ltrp.job.mechanic.MechanicJob;
import lt.ltrp.job.medic.MedicJob;
import lt.ltrp.job.policeman.OfficerJob;
import lt.ltrp.job.trashman.TrashManJob;
import lt.ltrp.job.trashman.TrashMission;
import lt.ltrp.job.trashman.TrashMissions;
import lt.ltrp.job.vehiclethief.VehicleThiefJob;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class MySqlJobDaoImpl implements JobDao {

    private static final Logger logger = LoggerFactory.getLogger(MySqlJobDaoImpl.class);

    private DataSource dataSource;
    private VehicleDao vehicleDao;


    public MySqlJobDaoImpl(DataSource dataSource, VehicleDao vehicleDao) {
        this.dataSource = dataSource;
        this.vehicleDao = vehicleDao;
    }

    private void loadJob(Job job) throws LoadingException {
        String sql = "SELECT " +
                "jobs.*, " +
                "job_properties.key, job_properties.value, " +
                "job_leaders.player_id AS leader_id " +
                "FROM jobs " +
                "LEFT JOIN job_properties ON job_properties.job_id = jobs.id " +
                "LEFT JOIN job_leaders ON jobs.id = job_leaders.job_id " +
                "WHERE jobs.id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, job.getId());
            ResultSet result = stmt.executeQuery();
            Properties properties = new Properties();
            if(result.next()) {
                job.setName(result.getString("name"));
                job.setLocation(new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getInt("interior"), result.getInt("virtual_world")));
                job.setBasePaycheck(result.getInt("paycheck"));
                job.setVehicles(getVehicles(job));
                job.setRanks(getRanks(job));
                String key = result.getString("key"),
                        value = result.getString("value");
                if(key != null && value != null)
                    properties.setProperty(key, value);
            }
            while(result.next()) {
                properties.setProperty(result.getString("key"), result.getString("value"));
                int leaderId = result.getInt("leader_id");
                if(!result.wasNull()) {
                    if(job instanceof Faction) {
                        ((Faction) job).addLeader(leaderId);
                    } else {
                        logger.error("Job " + job.getId() + " contains leaders, but does not inherit from 'Faction'");
                    }
                }
            }
            for(Field f : job.getClass().getFields()) {
                if(f.isAnnotationPresent(JobProperty.class)) {
                    JobProperty jobProperty = f.getAnnotation(JobProperty.class);
                    String name = jobProperty.value();
                    if(properties.containsKey(name)) {
                        try {
                            if(f.getType().getName().equals("int")) {
                                f.setInt(job, Integer.parseInt(properties.getProperty(name)));
                            } else if(f.getType().getName().equals("float")) {
                                f.setFloat(job, Float.parseFloat(properties.getProperty(name)));
                            } else {
                                f.set(job, properties.getProperty(name));
                            }

                        } catch(IllegalAccessException e) {
                            throw new LoadingException("Could not access field " + f.getName() + " from " + job.getClass(), e);
                        }
                    } else {
                        throw new LoadingException("Missing property " + name + " from " + job.getClass());
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DrugDealerJob getDrugDealerJob(int id) throws LoadingException {
        DrugDealerJob job = new DrugDealerJob(id);
        loadJob(job);
        return job;
    }

    @Override
    public MechanicJob getMechanicJob(int id) throws LoadingException {
        MechanicJob job = new MechanicJob(id);
        loadJob(job);
        return job;
    }

    @Override
    public MedicJob getMedicJob(int id) throws LoadingException {
        MedicJob job = new MedicJob(id);
        loadJob(job);
        return job;
    }

    @Override
    public OfficerJob getOfficerJob(int id) throws LoadingException {
        OfficerJob job = new OfficerJob(id);
        loadJob(job);
        return job;
    }

    @Override
    public TrashManJob getTrashmanJob(int id) throws LoadingException {
        TrashManJob job = new TrashManJob(id);
        loadJob(job);
        return job;
    }

    @Override
    public VehicleThiefJob getVehicleThiefJob(int id) throws LoadingException {
        VehicleThiefJob job = new VehicleThiefJob(id);
        loadJob(job);
        return job;
    }

    @Override
    public void addLeader(Faction faction, int userid) {
        String sql = "INSERT INTO job_leaders (job_id, player_id) VALUES (?, ?) ";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, faction.getId());
            stmt.setInt(2, userid);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeLeader(Faction faction, int userid) {
        String sql = "DELETE FROM job_leaders WHERE job_id = ? AND player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, faction.getId());
            stmt.setInt(2, userid);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Rank> getEmployeeList(Job job) {
        Map<String, Rank> ranks = new HashMap<>();
        String sql = "SELECT players.username, job_ranks.*, job_ranks_contract.* FROM players " +
                "LEFT JOIN job_ranks ON players.job_id = job_ranks.job_id " +
                "LEFT JOIN job_ranks_contract ON job_ranks.id = job_ranks_contract.id AND job_ranks.job_id = job_ranks_contract.job_id " +
                "WHERE players.job_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, job.getId());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                Rank rank;
                int number = r.getInt("number");
                String name = r.getString("name");
                int salary = r.getInt("salary");
                int xp_needed = r.getInt("xp_needed");
                if(!r.wasNull()) {
                    rank = new ContractJobRank(number, xp_needed, name, salary);
                } else {
                    rank = new FactionRank(number, name, salary);
                }
                ranks.put(r.getString("username"), rank);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return ranks;
    }

    @Override
    public Collection<JobVehicle> getVehicles(Job job) throws LoadingException{
        String sql = "SELECT vehicles.*, job_ranks.id AS rank_id, job_ranks.name, job_ranks.salary, job_ranks_contract.xp_needed FROM job_vehicles " +
                "LEFT JOIN vehicles ON vehicles.id = job_vehicles.id " +
                "LEFT JOIN job_ranks ON job_ranks.id = job_vehicles.rank_id " +
                "LEFT JOIN job_ranks_contract ON job_ranks.id = job_ranks_contract.id " +
                "WHERE job_vehicles.job_id = ?";
        Collection<JobVehicle> vehicles = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, job.getId());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                Rank rank;
                if(job instanceof ContractJob)
                    rank = new ContractJobRank(result.getInt("rank_id"), result.getInt("hours_needed"), result.getString("name"), result.getInt("salary"));
                else
                    rank = new FactionRank(result.getInt("rank_id"), result.getString("name"), result.getInt("salary"));
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
            throw new LoadingException("Could not job " + job.getId() + " vehicles", e);
        }
        return vehicles;
    }

    @Override
    public void insertVehicle(JobVehicle vehicle) {
        String jobVehicleSql = "INSERT INTO job_vehicles (id, job_id, rank_id) VALUES (?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement jobVehicleStmt = con.prepareStatement(jobVehicleSql);
        ) {
            vehicleDao.insert(vehicle);
            jobVehicleStmt.setInt(1, vehicle.getUniqueId());
            jobVehicleStmt.setInt(2, vehicle.getJob().getId());
            jobVehicleStmt.setInt(3, vehicle.getRequiredRank().getNumber());
            jobVehicleStmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeVehicle(JobVehicle vehicle) {
        vehicleDao.delete(vehicle);
       /* String sql = "DELETE FROM vehicles WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, vehicle.getUniqueId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void updateVehicle(JobVehicle vehicle) {
        String jobVehicleSql = "UPDATE job_vehicles SET job_id = ?, rank_id = ? WHERE id = ?";
         try (
                Connection con = dataSource.getConnection();

                PreparedStatement jobVehicleStmt = con.prepareStatement(jobVehicleSql);
        ) {
             vehicleDao.update(vehicle);
             jobVehicleStmt.setInt(1, vehicle.getJob().getId());
             jobVehicleStmt.setInt(2, vehicle.getRequiredRank().getNumber());
             jobVehicleStmt.setInt(3, vehicle.getUniqueId());
             jobVehicleStmt.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Rank> getRanks(Job job) throws LoadingException {
        // In this case it is sorted by the number, although this is not guaranteed
        Collection<Rank> ranks = new TreeSet<>((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber()));
        String sql = "SELECT job_ranks.*, job_ranks_contract.xp_needed FROm job_ranks LEFT JOIN job_ranks_contract ON job_ranks.id = job_ranks_contract.id WHERE job_ranks.job_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, job.getId());
            ResultSet resultSet = stmt.executeQuery();
            Rank rank;
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int xp = resultSet.getInt("xp_needed");
                int salary = resultSet.getInt("salary");
                if(resultSet.wasNull()) {
                    rank =  new FactionRank(id, name, salary);
                } else {
                    rank = new ContractJobRank(id, xp, name, salary);
                }
                rank.setJob(job);
                ranks.add(rank);

            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return ranks;
    }

    @Override
    public void insertRank(Rank rank) {
        String sql = "INSERT INTO job_ranks (id, job_id, name, salary) VALUES (?, ?, ?, ?)";
        String contractSql = "INSERT INTO job_ranks_contract (id, job_id, xp_needed) VALUES (?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                PreparedStatement contractStmt = con.prepareStatement(contractSql)
        ) {
            stmt.setInt(1, rank.getJob().getId());
            stmt.setInt(2, rank.getNumber());
            stmt.setString(3, rank.getName());
            stmt.setInt(4, rank.getSalary());
            stmt.execute();

            if(rank instanceof ContractJobRank) {
                contractStmt.setInt(1, rank.getNumber());
                contractStmt.setInt(2, rank.getJob().getId());
                contractStmt.setInt(3, ((ContractJobRank) rank).getXpNeeded());
                contractStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeRank(Rank rank) {
        String sql = "DELETE FROM job_ranks WHERE id = ? AND job_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, rank.getNumber());
            stmt.setInt(2, rank.getJob().getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TrashMissions getTrashMissions() {
        TrashMissions trashMissions = new TrashMissions();
        String sql = "SELECT garbageman_missions.*, garbageman_mission_garbage.* FROM garbageman_missions LEFT JOIN garbageman_mission_garbage ON garbageman_missions.id = garbageman_mission_garbage.mission_id";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            ResultSet result = stmt.executeQuery();

            TrashMission mission = null;
            while(result.next()) {
                int id = result.getInt("id");
                if(mission == null || mission.getId() != id) {
                    if(mission != null)
                        trashMissions.add(mission);
                    mission = new TrashMission(id, result.getString("name"));
                }
                mission.addGarbage(new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z")));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return trashMissions;
    }

}
