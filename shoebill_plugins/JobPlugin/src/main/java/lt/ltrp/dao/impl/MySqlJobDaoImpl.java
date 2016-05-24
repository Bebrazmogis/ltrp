package lt.ltrp.dao.impl;


import lt.ltrp.ContractJobRankImpl;
import lt.ltrp.FactionRankImpl;
import lt.ltrp.JobProperty;
import lt.ltrp.LoadingException;
import lt.ltrp.dao.JobDao;
import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.object.ContractJobRank;
import lt.ltrp.object.Faction;
import lt.ltrp.object.Job;
import lt.ltrp.object.Rank;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;
import java.util.TreeSet;

/**
 * @author Bebras
 *         2016.03.01.
 */
public abstract class MySqlJobDaoImpl implements JobDao {

    private static final Logger logger = LoggerFactory.getLogger(MySqlJobDaoImpl.class);

    private DataSource dataSource;
    private EventManager eventManager;
    private JobVehicleDao jobVehicleDao;


    public MySqlJobDaoImpl(DataSource dataSource, JobVehicleDao jobVehicleDao, EventManager eventManager) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
        if(jobVehicleDao == null) {
            this.jobVehicleDao = new MySqlJobVehicleDao(dataSource, eventManager);
        }
        else {
            this.jobVehicleDao = jobVehicleDao;
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    protected void load(Job job) throws LoadingException {
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
            stmt.setInt(1, job.getUUID());
            ResultSet result = stmt.executeQuery();
            Properties properties = new Properties();
            if(result.next()) {
                job.setName(result.getString("name"));
                job.setLocation(new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getInt("interior"), result.getInt("virtual_world")));
                job.setBasePaycheck(result.getInt("paycheck"));
                job.setVehicles(jobVehicleDao.get(job));
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
                        logger.error("Job " + job.getUUID() + " contains leaders, but does not inherit from 'Faction'");
                    }
                }
            }
            for(Field f : job.getClass().getFields()) {
                if(f.isAnnotationPresent(JobProperty.class)) {
                    JobProperty jobProperty = f.getAnnotation(JobProperty.class);
                    String name = jobProperty.value();
                    // || dirty fix to allow specifying only the prefix
                    if(properties.containsKey(name) || properties.stringPropertyNames().stream().filter(s -> s.startsWith(name + "_")).findFirst().isPresent()) {
                        try {
                            if(f.getType().getName().equals("int")) {
                                f.setInt(job, Integer.parseInt(properties.getProperty(name)));
                            } else if(f.getType().getName().equals("float")) {
                                f.setFloat(job, Float.parseFloat(properties.getProperty(name)));
                            } else if(f.getType().equals(Location.class)) {
                                Location location = new Location(Float.parseFloat(properties.getProperty(name + "_x")),
                                        Float.parseFloat(properties.getProperty(name + "_y")),
                                        Float.parseFloat(properties.getProperty(name + "_z")),
                                        Integer.parseInt(properties.getProperty(name + "_interior", "0")),
                                        Integer.parseInt(properties.getProperty(name + "_world", "0")));
                                f.set(job, location);
                            } else {
                                f.set(job, properties.getProperty(name));
                            }
                        } catch(IllegalAccessException e) {
                            throw new LoadingException("Could not access field " + f.getName() + " from " + job.getClass(), e);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        logger.warn("Job "+  job.getUUID() + " (" + job.getName() + ") property " + name + " missing.");
                        //throw new LoadingException("Missing property " + name + " from " + job.getClass());
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public DrugDealerJob getDrugDealerJob(int id) throws LoadingException {
        DrugDealerJob job = new DrugDealerJobImpl(id, eventManager);
        loadJob(job);
        return job;
    }

    @Override
    public MechanicJob getMechanicJob(int id) throws LoadingException {
        MechanicJob job = new MechanicJobImpl(id, eventManager);
        loadJob(job);
        return job;
    }

    @Override
    public MedicFaction getMedicJob(int id) throws LoadingException {
        MedicFactionImpl job = new MedicFactionImpl(id, eventManager);
        loadJob(job);
        return job;
    }

    @Override
    public PoliceFaction getOfficerJob(int id) throws LoadingException {
        PoliceFactionImpl job = new PoliceFactionImpl(id, eventManager);
        loadJob(job);
        return job;
    }

    @Override
    public TrashManJob getTrashmanJob(int id) throws LoadingException {
        TrashManJob job = new TrashManJobImpl(id, eventManager);
        loadJob(job);
        return job;
    }
    */
/*
    @Override
    public VehicleThiefJob getVehicleThiefJob(int id) throws LoadingException {
        VehicleThiefJob job = new VehicleThiefJobImpl(id, eventManager);
        loadJob(job);
        return job;
    }*/

    /*
    @Override
    public void addLeader(Faction faction, int userid) {
        String sql = "INSERT INTO job_leaders (job_id, player_id) VALUES (?, ?) ";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, faction.getUUID());
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
            stmt.setInt(1, faction.getUUID());
            stmt.setInt(2, userid);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
*//*
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
            stmt.setInt(1, job.getUUID());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                Rank rank;
                int number = r.getInt("number");
                String name = r.getString("name");
                int salary = r.getInt("salary");
                int xp_needed = r.getInt("xp_needed");
                if(!r.wasNull()) {
                    rank = new ContractJobRankImpl(number, xp_needed, name, salary);
                } else {
                    rank = new FactionRankImpl(number, name, salary);
                }
                ranks.put(r.getString("username"), rank);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return ranks;
    }

*/
    @Override
    public Collection<Rank> getRanks(Job job) throws LoadingException {
        // In this case it is sorted by the number, although this is not guaranteed
        Collection<Rank> ranks = new TreeSet<>((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber()));
        String sql = "SELECT job_ranks.*, job_ranks_contract.xp_needed FROm job_ranks LEFT JOIN job_ranks_contract ON job_ranks.id = job_ranks_contract.id WHERE job_ranks.job_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, job.getUUID());
            ResultSet resultSet = stmt.executeQuery();
            Rank rank;
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int xp = resultSet.getInt("xp_needed");
                int salary = resultSet.getInt("salary");
                if(resultSet.wasNull()) {
                    rank =  new FactionRankImpl(id, name, salary);
                } else {
                    rank = new ContractJobRankImpl(id, xp, name, salary);
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
            stmt.setInt(1, rank.getJob().getUUID());
            stmt.setInt(2, rank.getNumber());
            stmt.setString(3, rank.getName());
            stmt.setInt(4, rank.getSalary());
            stmt.execute();

            if(rank instanceof ContractJobRank) {
                contractStmt.setInt(1, rank.getNumber());
                contractStmt.setInt(2, rank.getJob().getUUID());
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
            stmt.setInt(2, rank.getJob().getUUID());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
/*
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
*/

    @Override
    public boolean isValid(int jobId) {
        String sql = "SELECT id FROM jobs WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, jobId);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(Job job) {

    }

}
