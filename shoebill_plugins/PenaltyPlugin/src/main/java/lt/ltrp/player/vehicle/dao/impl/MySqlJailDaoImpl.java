package lt.ltrp.player.vehicle.dao.impl;

import lt.ltrp.dao.JailDao;
import lt.ltrp.data.JailData;
import lt.ltrp.object.LtrpPlayer;

import javax.sql.DataSource;
import java.sql.*;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class MySqlJailDaoImpl implements JailDao {

    private DataSource dataSource;

    public MySqlJailDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(JailData data) {
        String sql = "INSERT INTO player_jailtime (player_id, seconds_total, seconds_remaining, type, jailer, created_at) VALUEs (?, ?, ?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) {
            stmt.setInt(1, data.getPlayer().getUUID());
            stmt.setInt(2, data.getTotalTime());
            stmt.setInt(3, data.getRemainingTime());
            stmt.setString(4, data.getType().name());
            stmt.setInt(5, data.getJailer());
            stmt.setTimestamp(6, Timestamp.valueOf(data.getDate()));
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                data.setId(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JailData get(LtrpPlayer player) {
        JailData jailData = null;
        String sql = "SELECT * FROM player_jailtime WHERE player_id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, player.getUUID());
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                jailData = new JailData(r.getInt("id"),
                        player,
                        JailData.JailType.valueOf(r.getString("type")),
                        r.getInt("seconds_remaining"),
                        r.getInt("seconds_total"),
                        r.getInt("jailer"),
                        r.getTimestamp("created_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jailData;
    }

    @Override
    public void update(JailData jailData) {
        String sql = "UPDATE player_jailtime SET seconds_total = ?, seconds_remaining = ?, type = ?, jailer = ?, created_at = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, jailData.getTotalTime());
            stmt.setInt(2, jailData.getRemainingTime());
            stmt.setString(3, jailData.getType().name());
            stmt.setInt(4, jailData.getJailer());
            stmt.setTimestamp(5, Timestamp.valueOf(jailData.getDate()));
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(JailData data) {
        String sql = "DELETE FROM player_jailtime WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, data.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
