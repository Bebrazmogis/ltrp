package lt.ltrp.player.job.dao.impl;

import lt.ltrp.job.JobController;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.object.PlayerData;
import lt.ltrp.player.job.dao.PlayerJobDao;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.player.object.LtrpPlayer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

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
        String sql = "UPDATE job_employees SET job_id = ?, rank_id = ?, job_level = ?, experience = ?, hours = ?, remaining_contract = ? WHERE player_id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, jobData.getJob().getUUID());
            stmt.setInt(2, jobData.getRank().getUUID());
            stmt.setInt(3, jobData.getLevel());
            stmt.setInt(4, jobData.getXp());
            stmt.setInt(5, jobData.getHours());
            stmt.setInt(6, jobData.getRemainingContract());
            stmt.setInt(7, jobData.getPlayer().getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(PlayerJobData jobData) {
        String sql = "DELETE FROM job_employees WHERE player_id = ?";
        try (
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, jobData.getPlayer().getUUID());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(PlayerJobData jobData) {
        String sql = "INSERT INTO job_employees (job_id, player_id, rank_id, job_level, experience, hours, remaining_contract) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
                ) {
            stmt.setInt(1, jobData.getJob().getUUID());
            stmt.setInt(2, jobData.getPlayer().getUUID());
            stmt.setInt(3, jobData.getRank().getUUID());
            stmt.setInt(4, jobData.getLevel());
            stmt.setInt(5, jobData.getXp());
            stmt.setInt(6, jobData.getHours());
            stmt.setInt(7, jobData.getRemainingContract());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerJobData get(PlayerData player) {
        String sql = "SELECT * FROM job_employees WHERE player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
            ResultSet result = stmt.executeQuery();
            if(result.next())
                return toPlayerJobData(result, player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PlayerJobData toPlayerJobData(ResultSet r, PlayerData player) throws SQLException {
        Job job = JobController.instance.get(r.getInt("job_id"));
        if(job != null) {
            JobRank rank = job.getRankByUUID(r.getInt("rank_id"));
            if(r.wasNull()) {
                Optional<? extends JobRank> opRank = job.getRanks().stream().min((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber()));
                if(opRank.isPresent())
                    rank = opRank.get();
            }
            return new PlayerJobData(player, job, rank,
                    r.getInt("job_level"),
                    r.getInt("experience"),
                    r.getInt("hours"),
                    r.getInt("remaining_contract")
            );
        }
        return null;
    }
/*
    @Override
    public Map<String, JobRank> getEmployeeList(Job job) {
        Map<String, JobRank> ranks = new HashMap<>();
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
                JobRank rank;
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
    }*/


}
