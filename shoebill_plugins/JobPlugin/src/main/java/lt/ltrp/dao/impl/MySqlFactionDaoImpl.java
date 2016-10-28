package lt.ltrp.dao.impl;

import lt.ltrp.object.impl.FactionRankImpl;
import lt.ltrp.LoadingException;
import lt.ltrp.job.dao.JobVehicleDao;
import lt.ltrp.job.object.Faction;
import lt.ltrp.job.object.FactionRank;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Bebras
 *         2016.05.23.
 */
/*
public class MySqlFactionDaoImpl extends MySqlJobDaoImpl implements FactionDao {


    public MySqlFactionDaoImpl(DataSource dataSource, JobVehicleDao vehicleDao, EventManager eventManager) {
        super(dataSource, vehicleDao, null, eventManager);
    }


    @Override
    public void addLeader(Faction faction, int userid) {
        String sql = "INSERT INTO job_leaders (job_id, player_id) VALUES (?, ?) ";
        try (
                Connection con = getDataSource().getConnection();
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
                Connection con = getDataSource().getConnection();
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
    public Collection<FactionRank> getRanks(Faction faction) throws LoadingException {
            // In this case it is sorted by the number, although this is not guaranteed
        Collection<FactionRank> ranks = new TreeSet<>((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber()));
        String sql = "SELECT * FROm job_ranks WHERE job_ranks.job_id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, faction.getUUID());
            ResultSet resultSet = stmt.executeQuery();
            FactionRank rank;
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int salary = resultSet.getInt("salary");
                rank =  new FactionRankImpl(id, name, salary);
                rank.setJob(faction);
                ranks.add(rank);

            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return ranks;

    }

    @Override
    public void insertRank(FactionRank factionRank) {
        String sql = "INSERT INTO job_ranks (id, job_id, name, salary) VALUES (?, ?, ?, ?)";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, factionRank.getJob().getUUID());
            stmt.setInt(2, factionRank.getNumber());
            stmt.setString(3, factionRank.getName());
            stmt.setInt(4, factionRank.getSalary());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeRank(FactionRank factionRank) {
        super.removeRank(factionRank);
    }

    @Override
    public Faction get(int i) {
        return null;
    }

    @Override
    public void update(Faction faction) {

    }
}*/
