package lt.ltrp.dao.impl;

import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.constant.PlayerVehiclePermission;
import lt.ltrp.dao.PlayerVehicleDao;
import lt.ltrp.data.FuelTank;
import lt.ltrp.data.PlayerVehicleMetadata;
import lt.ltrp.data.VehicleCrime;
import lt.ltrp.data.VehicleLock;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.object.VehicleAlarm;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.23.
 */
public class MySqlPlayerVehicleDaoImpl extends AbstractMySqlVehicleDaoImpl implements PlayerVehicleDao {

    public MySqlPlayerVehicleDaoImpl(DataSource ds, EventManager eventManager) {
        super(ds, eventManager);
    }

    @Override
    public int getPlayerVehicleCount(LtrpPlayer player) {
        String sql = "SELECT COUNT(id) FROM player_vehicles WHERE owner_id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
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
                Connection con = getDataSource().getConnection();
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
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleId);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {

                // alarm
                VehicleAlarm alarm = null;
                String alarmClass = r.getString("alarm");
                /*
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
                }*/

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
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, owner.getUUID());
            stmt.setInt(2, vehicle.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertCrime(VehicleCrime crime) {
        String sql = "INSERT INTO player_vehicle_crimes (license_plate, crime, reporter, `date`, fine) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = getDataSource().getConnection();
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
    public int[] getPlayerVehicles(LtrpPlayer player) {
        Collection<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM player_vehicles WHERE EXISTS (SELECT id FROM player_vehicle_permissions WHERE player_id = ? AND vehicle_id = player_vehicles.id LIMIT 1)";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, player.getUUID());
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
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, player.getUUID());
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
                Connection con = getDataSource().getConnection();
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
                Connection con = getDataSource().getConnection();
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
                        r.getFloat("health"),
                        getEventManager()
                );
                // alarm
                String alarmClass = r.getString("alarm");
                /*if(!r.wasNull()) {
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
                }*/
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
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getUUID());
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
                Connection con = getDataSource().getConnection();
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
            stmt.setInt(13, playerVehicle.getUUID());
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
                Connection con = getDataSource().getConnection();
        ) {
            for(String s : sqls) {
                try (
                        PreparedStatement stmt = con.prepareStatement(s);
                ) {
                    stmt.setInt(1, vehicle.getUUID());
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
