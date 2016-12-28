package lt.ltrp.business.dao.impl;


import lt.ltrp.constant.JobProperty;
import lt.ltrp.dao.DaoException;
import lt.ltrp.job.dao.JobDao;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.object.Entity;
import lt.ltrp.object.PlayerData;
import lt.ltrp.player.PlayerController;
import lt.ltrp.player.job.data.PlayerJobData;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * @author Bebras
 *         2016.03.01.
 *
 * A MySQL implementation of the {@link lt.ltrp.job.dao.JobDao} interface.
 * All {@link lt.ltrp.job.object.Job} implementation DAOs should extend this class
 */
public final class MySqlJobDaoImpl implements JobDao {
    private DataSource dataSource;
    private EventManager eventManager;


    public MySqlJobDaoImpl(DataSource dataSource, EventManager eventManager) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public boolean isValid(int jobId) throws DaoException {
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
            throw new DaoException("Can not validate job " + jobId, e);
        }
        return false;
    }

    @Override
    public boolean isValid(Job job) throws DaoException {
        return isValid(job.getUUID());
    }

    @Override
    public void update(Job job) throws DaoException {
        String sql = "UPDATE jobs SET name = ?, x = ?, y = ?, z = ?, interior = ?, virtual_world = ?, paycheck = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
                ) {
            stmt.setString(1, job.getName());
            stmt.setFloat(2, job.getLocation().getX());
            stmt.setFloat(3, job.getLocation().getY());
            stmt.setFloat(4, job.getLocation().getZ());
            stmt.setInt(5, job.getLocation().interiorId);
            stmt.setInt(6, job.getLocation().worldId);
            stmt.setInt(7, job.getBasePaycheck());
            stmt.setInt(8, job.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(Job job) throws DaoException {
        String sql = "INSERT INTO jobs (name, x, y, z, interior, virtual_world, paycheck) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setString(1, job.getName());
            stmt.setFloat(2, job.getLocation().getX());
            stmt.setFloat(3, job.getLocation().getY());
            stmt.setFloat(4, job.getLocation().getZ());
            stmt.setInt(5, job.getLocation().interiorId);
            stmt.setInt(6, job.getLocation().worldId);
            stmt.setInt(7, job.getBasePaycheck());
            stmt.execute();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if(keys.next())
                    return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Can not insert job \"" + job.getName() + "\"", e);
        }
        return Entity.Companion.getINVALID_ID();
    }

    @Override
    public void parseProperties(Job job) throws DaoException {
        Properties props = getProperties(job);
        // Job properties are injected into annotated fields
        for(Field field : job.getClass().getFields()) {
            if(!field.isAnnotationPresent(JobProperty.class))
                continue;

            JobProperty jobProperty = field.getAnnotation(JobProperty.class);
            // Should return the value "name" of the annotation
            String name = jobProperty.value();
            // || dirty fix to allow specifying only the prefix
            if(props.containsKey(name) || props.stringPropertyNames().stream().filter(s -> s.startsWith(name)).findFirst().isPresent()) {
                try {
                    if(field.getType().getName().equals("int")) {
                        field.setInt(job, Integer.parseInt(props.getProperty(name)));
                    } else if(field.getType().getName().equals("float")) {
                        field.setFloat(job, Float.parseFloat(props.getProperty(name)));
                    } else if(field.getType().equals(Location.class)) {
                        Location location = new Location(
                                Float.parseFloat(props.getProperty(name + "_x")),
                                Float.parseFloat(props.getProperty(name + "_y")),
                                Float.parseFloat(props.getProperty(name + "_z")),
                                Integer.parseInt(props.getProperty(name + "_interior", "0")),
                                Integer.parseInt(props.getProperty(name + "_world", "0"))
                        );
                        field.set(job, location);
                    } else {
                        field.set(job, props.getProperty(name));
                    }
                } catch (IllegalAccessException e) {
                    throw new DaoException("Could not set job property " + name + " for job " + job.getUUID(), e);
                } catch (NumberFormatException e) {
                    throw new DaoException("Invalid value \"" + props.getProperty(name) + "\" for \"" + name + "\" in job " + job.getUUID(), e);
                }
            }
        }
    }

    @Override
    public Collection<PlayerJobData> getEmployees(Job job) {
        Collection<PlayerJobData> employees = new ArrayList<>();
        String sql = "SELECT * FROM job_employees WHERE job_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, job.getUUID());
            try(ResultSet result = stmt.executeQuery()) {
                while(result.next()) {
                    PlayerData player = PlayerController.instance.getData(result.getInt("player_id"));
                    JobRank rank = job.getRankByUUID(result.getInt("rank_id"));
                    if(player == null || rank == null)
                        continue;
                    PlayerJobData jobData = new PlayerJobData(player,
                            job,
                            rank,
                            result.getInt("job_level"),
                            result.getInt("experience"),
                            result.getInt("hours"),
                            result.getInt("remaining_contract")
                    );
                    employees.add(jobData);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Could not retrieve employee list for " + job.getUUID(), e);
        }
        return employees;
    }

    private Properties getProperties(Job job) throws DaoException {
        String sql = "SELECT * FROM job_properties WHERE job_id = ?";
        Properties properties = new Properties();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, job.getUUID());
            try(ResultSet result = stmt.executeQuery()) {
                while(result.next()) {
                    properties.put(result.getString("key"), result.getString("value"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Can not retrieve job " + job.getUUID() + " properties", e);
        }
        return properties;
    }
}
