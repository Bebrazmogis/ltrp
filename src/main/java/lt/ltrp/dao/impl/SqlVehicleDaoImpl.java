package lt.ltrp.dao.impl;

import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.dao.VehicleDao;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.*;
import net.gtaun.shoebill.data.AngledLocation;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

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
    public int getPlayerVehicleByLicense(String licensePlate) {
        String sql = "SELECT id FROM vehicles WHERE license = ? AND EXISTS(SELECT id FROM player_vehicles WHERE player_vehicles.id = vehicles.id LIMIT 1)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, licensePlate);
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
    public PlayerVehicleMetadata getPlayerVehicleMeta(int vehicleId) {
        PlayerVehicleMetadata data = null;
        String sql = "SELECT  vehicles.id, vehicles.model, vehicles.fuel, vehicles.license, player_Vehicles.owner_id, player_vehicles.alarm," +
                "player_vehicles.lock_name, player_vehicles.lock_cracktime, player_vehicles.lock_price, player_vehicles.insurance, player_vehicles.deaths " +
                "FROM player_vehicles LEFT JOIN vehicles ON player_vehicles.id = vehicles.id " +
                "WHERE player_vehicles.id = ? ";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleId);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {

                // alarm
                VehicleAlarm alarm = null;
                String alarmClass = r.getString("alarm");
                if(!r.wasNull()) {
                    switch(alarmClass) {
                        case "SimpleAlarm":
                            alarm = new SimpleAlarm(null);
                            break;
                        case "PoliceAlertAlarm":
                            alarm = new PoliceAlertAlarm(null);
                            break;
                        case "PersonalAlarm":
                            alarm = new PersonalAlarm(null);
                            break;
                    }
                }

                data = new PlayerVehicleMetadata(
                        r.getInt("id"),
                        r.getInt("model"),
                        r.getInt("deaths"),
                        r.getFloat("fuel"),
                        r.getInt("owner_id"),
                        r.getString("license"),
                        alarm,
                        getLock(r),
                        r.getInt("insurance")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void setOwner(PlayerVehicle vehicle, LtrpPlayer owner) {
        String sql = "UPDATE player_vehicles SET owner_id = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, owner.getUserId());
            stmt.setInt(2, vehicle.getUniqueId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePermissions(PlayerVehicle vehicle, int userId) {
        String sql = "DELETE FROM player_vehicle_permissions WHERE vehicle_id = ? AND player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getUniqueId());
            stmt.setInt(2, userId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePermissions(PlayerVehicle vehicle) {
        String sql = "DELETE FROM player_vehicle_permissions WHERE vehicle_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getUniqueId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePermission(PlayerVehicle vehicle, int userId, PlayerVehiclePermission permission) {
        String sql = "DELETE FROM player_vehicle_permissions WHERE player_id = ? AND vehicle_id = ? AND permission = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, vehicle.getUniqueId());
            stmt.setString(3, permission.name());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPermission(PlayerVehicle vehicle, LtrpPlayer player, PlayerVehiclePermission permission) {
        addPermission(vehicle, player.getUserId(), permission);
    }

    @Override
    public void addPermission(PlayerVehicle vehicle, int userId, PlayerVehiclePermission permission) {
       addPermission(vehicle.getUniqueId(), userId, permission);
    }

    @Override
    public void addPermission(int vehicleId, int userId, PlayerVehiclePermission permission) {
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
    public Collection<PlayerVehiclePermission> getPermissions(int vehicleId, int userId) {
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
    public Map<Integer, PlayerVehiclePermission> getPermissions(int vehicleId) {
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
    public void insertArrest(int vehicleId, int arrestedById, String reason) {
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

    @Override
    public PlayerVehicleArrest getArrest(int vehicleId) {
        PlayerVehicleArrest arrest = null;
        String sql = "SELECT * FROM player_vehicle_arrests WHERE vehicle_id = ? AND deleted_at IS NULL LIMIT 1";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleId);
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
    public void removeArrest(PlayerVehicleArrest arrest) {
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
    public void update(LtrpVehicle vehicle) {
        String vehicleSql = "UPDATE vehicles SET model = ?, x = ?, y = ?, z = ?, angle = ?, license = ?, interior = ?, virtual_world = ?, color1 = ?, " +
                "color2 = ?, fuel = ?, mileage = ?  WHERE id = ?";
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
            vehicleStmt.setFloat(11, vehicle.getFuelTank().getFuel());
            vehicleStmt.setFloat(12, vehicle.getMileage());
            vehicleStmt.setInt(13, vehicle.getUniqueId());
            vehicleStmt.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(LtrpVehicle vehicle) {
        int id = insert(
                vehicle.getModelId(),
                vehicle.getSpawnLocation(),
                vehicle.getLicense(),
                vehicle.getColor1(),
                vehicle.getColor2(),
                vehicle.getFuelTank().getFuel(),
                vehicle.getMileage()
        );
        vehicle.setUniqueId(id);
        return id;
    }

    @Override
    public int insert(int modelId, AngledLocation location, String license, int color1, int color2, float fuel, float mileage) {
        String vehicleSql = "INSERT INTO vehicles (model, x, y, z, angle, license, interior, virtual_world, color1, color2, fuel, mileage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement vehicleStmt = con.prepareStatement(vehicleSql, com.mysql.jdbc.Statement.RETURN_GENERATED_KEYS);
        ) {
            vehicleStmt.setInt(1, modelId);
            vehicleStmt.setFloat(2, location.getX());
            vehicleStmt.setFloat(3, location.getY());
            vehicleStmt.setFloat(4, location.getZ());
            vehicleStmt.setFloat(5, location.getAngle());
            vehicleStmt.setString(6, license);
            vehicleStmt.setInt(7, location.getInteriorId());
            vehicleStmt.setInt(8, location.getWorldId());
            vehicleStmt.setInt(9, color1);
            vehicleStmt.setInt(10, color2);
            vehicleStmt.setFloat(11, fuel);
            vehicleStmt.setFloat(12, mileage);
            vehicleStmt.execute();
            ResultSet resultSet = vehicleStmt.getGeneratedKeys();
            int id = 0;
            if(resultSet.next()) {
                id = resultSet.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
    public int[] getPlayerVehicles(LtrpPlayer player) {
        Collection<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM player_vehicles WHERE EXISTS (SELECT id FROM player_vehicle_permissions WHERE player_id = ? AND vehicle_id = player_vehicles.id LIMIT 1)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, player.getUserId());
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        int count = 0;
        int[] ints = new int[ids.size()];
        for(int i : ids) {
            ints[count++] = i;
        }
        return ints;
    }

    @Override
    public int[] getArrestedPlayerVehicles(LtrpPlayer player) {
        Collection<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM player_vehicles WHERE " +
                "EXISTS (SELECT id FROM player_vehicle_permissions WHERE player_id = ? AND vehicle_id = player_vehicles.id LIMIT 1) " +
                "AND " +
                "EXISTS (SELECT id FROM player_vehicle_arrests WHERE player_vehicle_arrests.vehicle_id = player_vehicles.id)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, player.getUserId());
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                ids.add(resultSet.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        int count = 0;
        int[] ints = new int[ids.size()];
        for(int i : ids) {
            ints[count++] = i;
        }
        return ints;
    }

    @Override
    public int insert(int modelId, AngledLocation spawnLocation, String license, int color1, int color2, float mileage, float fuel, int ownerId, int deaths, String alarm, String lock, int lockCrackTime, int lockPrice, int insurance, int doors, int panels, int lights, int tires, float health) {
        String sql = "INSERT INTO player_vehicles (id, owner_id, deaths, alarm, lock_name, lock_cracktime, lock_price, insurance, doors, panels, lights, tires, health) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            int id = insert(modelId, spawnLocation, license, color1, color2, fuel, mileage);
            stmt.setInt(1, id);
            stmt.setInt(2, ownerId);
            stmt.setInt(3, deaths);
            stmt.setString(4, alarm);
            stmt.setString(5, lock);
            stmt.setInt(6, lockCrackTime);
            stmt.setInt(7, lockPrice);
            stmt.setInt(8, insurance);
            stmt.setInt(9, doors);
            stmt.setInt(10, panels);
            stmt.setInt(11, lights);
            stmt.setInt(12, tires);
            stmt.setFloat(13, health);
            stmt.execute();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public PlayerVehicle get(int vehicleId) {
        PlayerVehicle vehicle= null;
        String sql = "SELECT player_vehicles.*, vehicles.* FROM player_vehicles LEFT JOIN vehicles ON vehicles.id = player_vehicles.id WHERE player_vehicles.id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleId);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {

                vehicle = PlayerVehicle.create(
                        r.getInt("id"),
                        r.getInt("model"),
                        new AngledLocation(r.getFloat("x"),
                                r.getFloat("y"),
                                r.getFloat("z"),
                                r.getInt("interior"),
                                r.getInt("virtual_world"),
                                r.getFloat("angle")),
                        r.getInt("color1"),
                        r.getInt("color2"),
                        r.getInt("owner_id"),
                        r.getInt("deaths"),
                        new FuelTank(LtrpVehicleModel.getFuelTankSize(r.getInt("model")), r.getFloat("fuel")),
                        r.getFloat("mileage"),
                        r.getString("license"),
                        r.getInt("insurance"),
                        null,
                        null,
                        r.getInt("doors"),
                        r.getInt("panels"),
                        r.getInt("lights"),
                        r.getInt("tires"),
                        r.getFloat("health")
                );
                // alarm
                String alarmClass = r.getString("alarm");
                if(!r.wasNull()) {
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
                }
                vehicle.setLock(getLock(r));
                loadPermissions(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicle;
    }

    private void loadPermissions(PlayerVehicle vehicle) {
        String sql = "SELECT * FROM player_vehicle_permissions WHERE vehicle_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getUniqueId());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                vehicle.addPermission(r.getInt("player_id"), PlayerVehiclePermission.valueOf(r.getString("permission")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(PlayerVehicle playerVehicle) {
        String sql = "UPDATE player_vehicles SET owner_id = ?, deaths = ?, alarm = ?, lock_name = ?, lock_cracktime = ?, lock_price = ?, insurance = ?," +
                " doors = ?, panels = ?, lights = ?, tires = ?, health = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            update((LtrpVehicle)playerVehicle);
            String alarm = playerVehicle.getAlarm() != null ? playerVehicle.getAlarm().getClass().getSimpleName() : null;
            String lock;
            int lockCrack, lockPrice;
            if(playerVehicle.getLock() != null) {
                lock = playerVehicle.getLock().getName();
                lockCrack = playerVehicle.getLock().getCrackTime();
                lockPrice = playerVehicle.getLock().getPrice();
            } else {
                lock = null;
                lockCrack = 0;
                lockPrice = 0;
            }

            stmt.setInt(1, playerVehicle.getOwnerId());
            stmt.setInt(2, playerVehicle.getDeaths());
            stmt.setString(3, alarm);
            stmt.setString(4, lock);
            stmt.setInt(5, lockCrack);
            stmt.setInt(6, lockPrice);
            stmt.setInt(7, playerVehicle.getInsurance());
            stmt.setInt(8, playerVehicle.getDamage().getDoors());
            stmt.setInt(9, playerVehicle.getDamage().getPanels());
            stmt.setInt(10, playerVehicle.getDamage().getLights());
            stmt.setInt(11, playerVehicle.getDamage().getTires());
            stmt.setFloat(12, playerVehicle.getHealth());
            stmt.setInt(13, playerVehicle.getUniqueId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(PlayerVehicle vehicle) {
        String[] sqls = new String[]{
                "DELETE FROM player_vehicles WHERE id = ?",
                "DELETE FROM player_vehicle_permissions WHERE vehicle_id = ?"
        };
        try (
                Connection con = dataSource.getConnection();
        ) {
            for(String s : sqls) {
                try (
                        PreparedStatement stmt = con.prepareStatement(s);
                        ) {
                    stmt.setInt(1, vehicle.getUniqueId());
                    stmt.execute();
                }
            }
            delete((LtrpVehicle)vehicle);
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    private VehicleLock getLock(ResultSet r) throws SQLException {
        return new VehicleLock(r.getString("lock_name"), r.getInt("lock_cracktime"), r.getInt("lock_price"));
    }
}
