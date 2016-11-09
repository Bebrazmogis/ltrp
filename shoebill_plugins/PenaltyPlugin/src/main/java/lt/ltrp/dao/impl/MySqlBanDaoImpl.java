package lt.ltrp.dao.impl;

import lt.ltrp.dao.BanDao;
import lt.ltrp.data.BanData;
import lt.ltrp.object.LtrpPlayer;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class MySqlBanDaoImpl implements BanDao {

    private DataSource dataSource;

    public MySqlBanDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public BanData getByUser(int userId) {
        BanData banData = null;
        String sql = "SELECT * FROM bans WHERE player_id = ? AND deleted_at IS NULL";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                banData = resultToBan(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banData;
    }

    @Override
    public BanData getByIp(String ip) {
        BanData banData = null;
        String sql = "SELECT * FROM bans WHERE ip = ? AND deleted_at IS NULL";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, ip);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                banData = resultToBan(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banData;
    }

    @Override
    public BanData getByUserOrIp(int userId, String ip) {
        BanData banData = null;
        String sql = "SELECT * FROM bans WHERE (player_id = ? OR ip = ?) AND deleted_at IS NULL";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setString(2, ip);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                banData = resultToBan(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banData;
    }

    @Override
    public BanData getByName(String name) {
        String sql = "SELECT * FROM bans WHERE player_id = (SELECT id FROM players WHERE username = ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setString(1, name);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                return resultToBan(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(BanData banData) {
        String sql = "UPDATE bans SET ip = ?, banned_by = ?, reason = ?, duration = ?, created_at = ?, deleted_at = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, banData.getIp());
            if(banData.getAdminId() != LtrpPlayer.INVALID_USER_ID)
                stmt.setInt(2, banData.getAdminId());
            else
                stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, banData.getReason());
            if(banData.isPermanent())
                stmt.setNull(4, Types.INTEGER);
            else
                stmt.setInt(4, banData.getDuration());
            stmt.setTimestamp(5, Timestamp.valueOf(banData.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(banData.getDeletedAt()));
            stmt.setInt(7, banData.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(BanData banData) {
        String sql = "UPDATE bans SET created_at = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setDate(1, new Date(Instant.now().toEpochMilli()));
            stmt.setInt(2, banData.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(BanData banData) {
        String sql = "INSERT INTO bans (player_id, ip, banned_by, reason, duration, created_at, deleted_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, banData.getUserId());
            stmt.setString(2, banData.getIp());
            if(banData.getAdminId() != LtrpPlayer.INVALID_USER_ID)
                stmt.setInt(3, banData.getAdminId());
            else
                stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, banData.getReason());
            if(banData.isPermanent())
                stmt.setNull(5, Types.INTEGER);
            else
                stmt.setInt(5, banData.getDuration());
            stmt.setTimestamp(6, Timestamp.valueOf(banData.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(banData.getDeletedAt()));
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                banData.setUuid(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private BanData resultToBan(ResultSet r) throws SQLException {
        BanData b;
        int duration = r.getInt("duration");
        if(r.wasNull())
            duration = -1;
        int bannedBy = r.getInt("banned_by");
        if(r.wasNull())
            bannedBy = LtrpPlayer.INVALID_USER_ID;
        b = new BanData(r.getInt("id"),
                r.getInt("player_id"),
                bannedBy,
                r.getString("reason"),
                r.getString("ip"),
                duration,
                r.getTimestamp("created_at").toLocalDateTime(),
                r.getTimestamp("deleted_at").toLocalDateTime());
        return b;
    }
}
