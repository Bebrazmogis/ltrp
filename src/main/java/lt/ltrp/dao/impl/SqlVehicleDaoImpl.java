package lt.ltrp.dao.impl;

import com.mysql.jdbc.*;
import lt.ltrp.dao.VehicleDao;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.*;
import net.gtaun.shoebill.data.AngledLocation;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Bebras
 *         2015.12.20.
 */
public class  SqlVehicleDaoImpl implements VehicleDao {

    private DataSource dataSource;

    public SqlVehicleDaoImpl(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public List<PlayerVehicle> getVehicles(LtrpPlayer player) {
        return null;
    }


    @Override
    public void update(PlayerVehicle playerVehicle) {
        String sql = "UPDATE player_vehicles SET owner_id = ?, model_id = ?, x = ?, y = ?, z = ?, angle = ?, color1 = ?, color2 = ?, deaths = ?, " +
                "alarm = ?, lock_name = ?, lock_cracktime = ?, lock_price = ?, insurance = ?, license_plate = ?, doors = ?, panels = ?, lights = ?," +
                " tires = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, playerVehicle.getOwnerId());
            stmt.setInt(2, playerVehicle.getModelId());
            stmt.setFloat(3, playerVehicle.getSpawnLocation().getX());
            stmt.setFloat(4, playerVehicle.getSpawnLocation().getY());
            stmt.setFloat(5, playerVehicle.getSpawnLocation().getZ());
            stmt.setFloat(6, playerVehicle.getSpawnLocation().getAngle());
            stmt.setInt(7, playerVehicle.getColor1());
            stmt.setInt(8, playerVehicle.getColor2());
            stmt.setInt(9, playerVehicle.getDeaths());
            stmt.setString(10, playerVehicle.getAlarm().getClass().getTypeName());
            stmt.setString(11, playerVehicle.getLock().getName());
            stmt.setInt(12, playerVehicle.getLock().getCrackTime());
            stmt.setInt(13, playerVehicle.getLock().getPrice());
            stmt.setInt(14, playerVehicle.getInsurance());
            stmt.setString(15, playerVehicle.getLicense());
            stmt.setInt(16, playerVehicle.getDoorsDmg());
            stmt.setInt(17, playerVehicle.getPanels());
            stmt.setInt(18, playerVehicle.getLights());
            stmt.setInt(19, playerVehicle.getTires());
            stmt.setInt(20, playerVehicle.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(PlayerVehicle playerVehicle) {
        String sql = "INSERT INTO player_vehicles (owner_id, model_id, x, y, z, angle, color1, color2, deaths, alarm, lock_name, lock_cracktime, " +
                "lock_price, insurance, license_plate, doors, panels, lights, tires) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            stmt.setInt(1, playerVehicle.getOwnerId());
            stmt.setInt(2, playerVehicle.getModelId());
            stmt.setFloat(3, playerVehicle.getSpawnLocation().getX());
            stmt.setFloat(4, playerVehicle.getSpawnLocation().getY());
            stmt.setFloat(5, playerVehicle.getSpawnLocation().getZ());
            stmt.setFloat(6, playerVehicle.getSpawnLocation().getAngle());
            stmt.setInt(7, playerVehicle.getColor1());
            stmt.setInt(8, playerVehicle.getColor2());
            stmt.setInt(9, playerVehicle.getDeaths());
            stmt.setString(10, playerVehicle.getAlarm().getClass().getTypeName());
            stmt.setString(11, playerVehicle.getLock().getName());
            stmt.setInt(12, playerVehicle.getLock().getCrackTime());
            stmt.setInt(13, playerVehicle.getLock().getPrice());
            stmt.setInt(14, playerVehicle.getInsurance());
            stmt.setString(15, playerVehicle.getLicense());
            stmt.setInt(16, playerVehicle.getDoorsDmg());
            stmt.setInt(17, playerVehicle.getPanels());
            stmt.setInt(18, playerVehicle.getLights());
            stmt.setInt(19, playerVehicle.getTires());
            stmt.execute();
            ResultSet result = stmt.getGeneratedKeys();
            if(result.next()) {
                playerVehicle.setId(result.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generateLicensePlate() {
        String sql = "SELECT license_plate FROM player_vehicles";
        List<String> plates = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                Statement stmt = con.createStatement();
                ) {
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()) {
                plates.add(result.getString("license_plate"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        String plate = null;
        Random random = new Random();
        while(plate == null || plates.contains(plate)) {
            plate = String.format("%3d-%c%c%c", random.nextInt(1000), (char)random.nextInt(24)+65, (char)random.nextInt(24)+65, (char)random.nextInt(24)+65);
        }
        return plate;
    }

    @Override
    public int getPlayerVehicleCount(LtrpPlayer player) {
        String sql = "SELECT COUNT(id) FROM player_vehicles WHERE owner_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                return result.getInt(1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void insertCrime(VehicleCrime crime) {
        String sql = "INSERT INTO player_vehicle_crimes (license_plate, crime, reporter, `date`, fine) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, crime.getLicensePlate());
            stmt.setString(2, crime.getCrime());
            stmt.setString(3, crime.getReporter());
            stmt.setDate(4, new Date(crime.getDate().getTime()));
            stmt.setInt(5, crime.getFine());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()) {
                crime.setId(keys.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(LtrpVehicle vehicle) {
        String vehicleSql = "UPDATE vehicles SET model = ?, x = ?, y = ?, z = ?, angle = ?, license = ?, interior = ?, virtual_world = ?, color1 = ?, color2 = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement vehicleStmt = con.prepareStatement(vehicleSql);
        ) {
            vehicleStmt.setInt(1, vehicle.getModelId());
            vehicleStmt.setFloat(2, vehicle.getSpawnLocation().getX());
            vehicleStmt.setFloat(3, vehicle.getSpawnLocation().getY());
            vehicleStmt.setFloat(4, vehicle.getSpawnLocation().getZ());
            vehicleStmt.setFloat(5, vehicle.getSpawnLocation().getAngle());
            vehicleStmt.setString(6, vehicle.getLicense());
            vehicleStmt.setInt(7, vehicle.getSpawnLocation().getInteriorId());
            vehicleStmt.setInt(8, vehicle.getSpawnLocation().getWorldId());
            vehicleStmt.setInt(9, vehicle.getColor1());
            vehicleStmt.setInt(10, vehicle.getColor2());
            vehicleStmt.setInt(11, vehicle.getUniqueId());
            vehicleStmt.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(LtrpVehicle vehicle) {
        String vehicleSql = "INSERT INTO vehicles (model, x, y, z, angle, license, interior, virtual_world, color1, color2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement vehicleStmt = con.prepareStatement(vehicleSql, com.mysql.jdbc.Statement.RETURN_GENERATED_KEYS);
        ) {
            vehicleStmt.setInt(1, vehicle.getModelId());
            vehicleStmt.setFloat(2, vehicle.getSpawnLocation().getX());
            vehicleStmt.setFloat(3, vehicle.getSpawnLocation().getY());
            vehicleStmt.setFloat(4, vehicle.getSpawnLocation().getZ());
            vehicleStmt.setFloat(5, vehicle.getSpawnLocation().getAngle());
            vehicleStmt.setString(6, vehicle.getLicense());
            vehicleStmt.setInt(7, vehicle.getSpawnLocation().getInteriorId());
            vehicleStmt.setInt(8, vehicle.getSpawnLocation().getWorldId());
            vehicleStmt.setInt(9, vehicle.getColor1());
            vehicleStmt.setInt(10, vehicle.getColor2());
            vehicleStmt.execute();
            ResultSet resultSet = vehicleStmt.getGeneratedKeys();
            if(resultSet.next()) {
                int id = resultSet.getInt(1);
                vehicle.setUniqueId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(LtrpVehicle vehicle) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, vehicle.getUniqueId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerVehicle getPlayerVehicle(int id) {
        PlayerVehicle vehicle = new PlayerVehicle();
        String sql = "SELECT * FROM player_vehicles WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                vehicle.setId(result.getInt("id"));
                vehicle.setOwnerId(result.getInt("owner_id"));
                vehicle.setModelId(result.getInt("model_id"));
                vehicle.setSpawnLocation(new AngledLocation(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getFloat("angle")));
                vehicle.setColor(result.getInt("color1"), result.getInt("color2"));
                vehicle.setDeaths(result.getInt("deaths"));
                // alarm
                String alarmClass = result.getString("alarm");
                switch(alarmClass) {
                    case "SimpleAlarm":
                        vehicle.setAlarm(new SimpleAlarm(vehicle));
                        break;
                    case "PoliceAlertAlarm":
                        vehicle.setAlarm(new PoliceAlertAlarm(vehicle));
                        break;
                    case "PersonalAlarm":
                        vehicle.setAlarm(new PersonalAlarm(vehicle));
                        break;
                }
                vehicle.setLock(new VehicleLock(result.getString("lock_name"), result.getInt("lock_cracktime"), result.getInt("price")));
                vehicle.setLicense(result.getString("license_plate"));
                vehicle.setDamageStatus(result.getInt("panels"), result.getInt("doors"), result.getInt("lights"), result.getInt("tires"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return vehicle;
    }
}
