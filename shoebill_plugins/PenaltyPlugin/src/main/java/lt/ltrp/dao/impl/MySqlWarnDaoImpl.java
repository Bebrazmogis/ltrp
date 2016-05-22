package lt.ltrp.dao.impl;

import lt.ltrp.dao.WarnDao;
import lt.ltrp.data.WarnData;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class MySqlWarnDaoImpl implements WarnDao {

    private DataSource dataSource;

    public MySqlWarnDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Collection<WarnData> get(int userId) {
        Collection<WarnData> warnData = new ArrayList<>();
        String sql = "SELECT * FROM player_warns WHERE player_id = ? AND deleted_at IS NULL";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                WarnData w = resultToWarn(r);
                warnData.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warnData;
    }

    @Override
    public int getCount(int userId) {
        String sql = "SELECT COUNT(id) FROM player_warns WHERE player_id = ? AND deleted_at IS NULL";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            ResultSet r = stmt.executeQuery();
            if(r.next())
                return r.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void remove(WarnData warnData) {
        String sql = "UPDATE player_warns SET deleted_at = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setDate(1, new Date(new java.util.Date().getTime()));
            stmt.setInt(2, warnData.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(WarnData warnData) {
        String sql = "INSERT INTO player_warns (player_id, warned_by, reason, created_at) VALUES (?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, warnData.getUserId());
            stmt.setInt(2, warnData.getWarnedByUserId());
            stmt.setString(3, warnData.getReason());
            stmt.setDate(4, warnData.getDate());
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                warnData.setId(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(WarnData warnData) {
        String sql = "UPDATE player_warns SET player_id = ?, warned_by = ?, reason = ?, created_at = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, warnData.getUserId());
            stmt.setInt(2, warnData.getWarnedByUserId());
            stmt.setString(3, warnData.getReason());
            stmt.setDate(4, warnData.getDate());
            stmt.setInt(5, warnData.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private WarnData resultToWarn(ResultSet r) throws SQLException {
        return new WarnData(r.getInt("id"), r.getInt("player_id"), r.getInt("warned_by"), r.getString("reason"), r.getDate("created_at"));
    }
}
