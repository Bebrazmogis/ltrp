package lt.ltrp.player.vehicle.dao.impl;

import lt.ltrp.player.vehicle.dao.PlayerVehicleFineDao;
import lt.ltrp.player.vehicle.data.VehicleFine;
import lt.ltrp.player.vehicle.object.PlayerVehicle;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class MySqlPlayerVehicleFineDaoImpl implements PlayerVehicleFineDao {

    private DataSource dataSource;

    public MySqlPlayerVehicleFineDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<VehicleFine> get(PlayerVehicle vehicle) {
        List<VehicleFine> fines = new ArrayList<>();
        String sql = " SELECT * FROM player_vehicle_fines WHERE vehicle_id = ? OR (license_plate IS NOT NULL AND license_plate = ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, vehicle.getUUID());
            stmt.setString(2, vehicle.getLicense());
            ResultSet r = stmt.executeQuery();
            while(r.next())
                fines.add(toFine(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    @Override
    public List<VehicleFine> getUnpaid(PlayerVehicle vehicle) {
        List<VehicleFine> fines = new ArrayList<>();
        String sql = " SELECT * FROM player_vehicle_fines WHERE vehicle_id = ? OR (license_plate IS NOT NULL AND license_plate = ?) AND paid_at IS NULL";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getUUID());
            stmt.setString(2, vehicle.getLicense());
            ResultSet r = stmt.executeQuery();
            while(r.next())
                fines.add(toFine(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    @Override
    public void update(VehicleFine fine) {
        String sql = "UPDATE player_vehicle_fines SET license_plate = ?, crime = ?, reporter = ?, created_at = ?, paid_at = ?, fine = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, fine.getLicense());
            stmt.setString(2, fine.getCrime());
            stmt.setString(3, fine.getReportedBy());
            stmt.setTimestamp(4, fine.getCreatedAt());
            stmt.setTimestamp(5, fine.getPaidAt());
            stmt.setInt(6, fine.getFine());
            stmt.setInt(7, fine.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPaid(VehicleFine fine) {
        String sql = "UPDATE player_vehicle_fines SET paid_at = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            if(fine.getPaidAt() != null)
                stmt.setTimestamp(1, fine.getPaidAt());
            else
                stmt.setTimestamp(1, new Timestamp(Instant.now().getEpochSecond()));
            stmt.setInt(2, fine.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(VehicleFine fine) {
        String sql = "INSERT INTO player_vehicle_fines (vehicle_id, license_plate, crime, reporter, created_at, paid_at, fine) VALUES (?, ?, ?, ? ,?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, fine.getVehicleUUID());
            stmt.setString(2, fine.getLicense());
            stmt.setString(3, fine.getCrime());
            stmt.setString(4, fine.getReportedBy());
            stmt.setTimestamp(5, fine.getCreatedAt());
            stmt.setTimestamp(6, fine.getPaidAt());
            stmt.setInt(7, fine.getFine());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next())
                return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private VehicleFine toFine(ResultSet r) throws SQLException {
        return new VehicleFine(
                r.getInt("id"),
                r.getInt("vehicle_id"),
                r.getString("license_plate"),
                r.getString("crime"),
                r.getString("reporter"),
                r.getTimestamp("created_at"),
                r.getTimestamp("paid_at"),
                r.getInt("fine")
        );
    }
}
