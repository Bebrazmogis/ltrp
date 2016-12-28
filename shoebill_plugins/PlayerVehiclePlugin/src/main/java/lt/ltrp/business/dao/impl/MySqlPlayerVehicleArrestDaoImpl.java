package lt.ltrp.business.dao.impl;

import lt.ltrp.dao.PlayerVehicleArrestDao;
import lt.ltrp.data.PlayerVehicleArrest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class MySqlPlayerVehicleArrestDaoImpl implements PlayerVehicleArrestDao {

    private DataSource dataSource;

    public MySqlPlayerVehicleArrestDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public PlayerVehicleArrest get(int vehicleUUID) {
        PlayerVehicleArrest arrest = null;
        String sql = "SELECT * FROM player_vehicle_arrests WHERE vehicle_id = ? AND deleted_at IS NULL LIMIT 1";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
            ) {
            stmt.setInt(1, vehicleUUID);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                arrest = new PlayerVehicleArrest(
                        r.getInt("id"),
                        r.getInt("vehicle_id"),
                        r.getInt("arrested_by"),
                        r.getString("reason"),
                        r.getTimestamp("created_at")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return arrest;
    }

    @Override
    public void remove(PlayerVehicleArrest arrest) {
        String sql = "DELETE FROM player_vehicle_arrests WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, arrest.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(int vehicleId, int arrestedById, String reason) {
        String sql = "INSERT INTO player_vehicle_arrests (vehicle_id, arrested_by, reason) VALUES (?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleId);
            stmt.setInt(2, arrestedById);
            stmt.setString(3, reason);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
