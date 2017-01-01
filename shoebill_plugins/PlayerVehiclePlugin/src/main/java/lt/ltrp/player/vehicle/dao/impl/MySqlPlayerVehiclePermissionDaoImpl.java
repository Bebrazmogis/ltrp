package lt.ltrp.player.vehicle.dao.impl;

import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission;
import lt.ltrp.player.vehicle.dao.PlayerVehiclePermissionDao;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class MySqlPlayerVehiclePermissionDaoImpl implements PlayerVehiclePermissionDao {
    
    private DataSource dataSource;

    public MySqlPlayerVehiclePermissionDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void remove(PlayerVehicle vehicle, int userId) {
        String sql = "DELETE FROM player_vehicle_permissions WHERE vehicle_id = ? AND player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getUUID());
            stmt.setInt(2, userId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(PlayerVehicle vehicle) {
        String sql = "DELETE FROM player_vehicle_permissions WHERE vehicle_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(PlayerVehicle vehicle, int userId, PlayerVehiclePermission permission) {
        String sql = "DELETE FROM player_vehicle_permissions WHERE player_id = ? AND vehicle_id = ? AND permission = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, vehicle.getUUID());
            stmt.setString(3, permission.name());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(PlayerVehicle vehicle, LtrpPlayer player, PlayerVehiclePermission permission) {
        add(vehicle, player.getUUID(), permission);
    }

    @Override
    public void add(PlayerVehicle vehicle, int userId, PlayerVehiclePermission permission) {
        add(vehicle.getUUID(), userId, permission);
    }

    @Override
    public void add(int vehicleId, int userId, PlayerVehiclePermission permission) {
        String sql = "INSERT INTO player_vehicle_permissions (player_id, vehicle_id, permission) VALUES (?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, vehicleId);
            stmt.setString(3, permission.name());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<PlayerVehiclePermission> get(int vehicleId, int userId) {
        Collection<PlayerVehiclePermission> permissions = new ArrayList<>();
        String sql = "SELECT permission FROM player_vehicle_permissions WHERE vehicle_id = ? AND player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleId);
            stmt.setInt(2, userId);
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                permissions.add(PlayerVehiclePermission.valueOf(resultSet.getString(1)));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    @Override
    public Map<Integer, PlayerVehiclePermission> get(int vehicleId) {
        Map<Integer, PlayerVehiclePermission> permissionMap = new HashMap<>();
        String sql = "SELECT player_id, permission FROM player_vehicle_permissions WHERE vehicle_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleId);
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                permissionMap.put(resultSet.getInt("player_id"), PlayerVehiclePermission.valueOf(resultSet.getString("permission")));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return permissionMap;
    }
}
