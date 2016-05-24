package lt.ltrp.dao.impl;

import lt.ltrp.ContractJobRankImpl;
import lt.ltrp.FactionRankImpl;
import lt.ltrp.dao.PlayerJobDao;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.Job;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Rank;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlPlayerJobDaoImpl implements PlayerJobDao {

    private DataSource dataSource;

    public MySqlPlayerJobDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void update(PlayerJobData jobData) {
        String sql = "UPDATE players SET ";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(PlayerJobData jobData) {

    }

    @Override
    public void insert(PlayerJobData jobData) {

    }

    @Override
    public PlayerJobData get(LtrpPlayer player) {
        return null;
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


}
