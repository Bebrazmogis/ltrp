package lt.ltrp.dao.impl;

import javafx.util.Pair;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.Animation;
import lt.ltrp.item.*;
import lt.ltrp.job.Job;
import lt.ltrp.player.*;
import lt.ltrp.vehicle.PlayerVehiclePermission;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bebras
 *         2015.11.12.
 */

public class SqlPlayerDaoImpl implements PlayerDao {

    private DataSource dataSource;

    public SqlPlayerDaoImpl(DataSource ds) {
        this.dataSource = ds;
    }


    @Override
    public int getUserId(Player player) {
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "SqlPlayerDaoImpl :: getUserId for player " + player.getName());
        int userid = LtrpPlayer.INVALID_USER_ID;
        String sql = "SELECT id FROM players WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ) {
            stmt.setString(1, player.getName());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "SqlPlayerDaoImpl :: getUserId userid is " + result.getInt("id"));
                userid = result.getInt("id");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return userid;
    }

    @Override
    public String getPassword(LtrpPlayer player) {
        String password = null;
        String sql = "SELECT `password` FROM players WHERE id = ? LIMIT 1";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                password = result.getString("password");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return password;
    }

    @Override
    public boolean loadData(LtrpPlayer player) {
        boolean loaded = false;
        String sql = "SELECT " +
                "secret_question, secret_answer, admin_level, players.level, players.job_id, money, connected_time, box_style, age, respect, bank_money, deaths, hunger " +
                ", player_job_levels.level AS job_level, player_job_levels.hours AS job_hours, player_job_levels.xp AS job_xp FROM players LEFT JOIN player_job_levels ON players.id = player_job_levels.player_id AND players.job_id = player_job_levels.job_id" +
                " WHERE players.id = ? LIMIT 1";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                player.setAdminLevel(result.getInt("admin_level"));
                player.setLevel(result.getInt("level"));
                player.setMoney(result.getInt("money"));
                player.setJob(Job.get(result.getInt("job_id")));
                player.setSecretQuestion(result.getString("secret_question"));
                player.setSecretAnswer(result.getString("secret_answer"));
                player.setConnectedTime(result.getInt("connected_time"));
                player.setBoxStyle(result.getInt("box_style"));
                player.setAge(result.getInt("age"));
                player.setRespect(result.getInt("respect"));
                player.setBankMoney(result.getInt("bank_money"));
                player.setDeaths(result.getInt("deaths"));
                player.setHunger(result.getInt("hunger"));

                int jobLevel = result.getInt("job_level");
                if(!result.wasNull()) {
                    player.setJobLevel(jobLevel);
                    player.setJobHours(result.getInt("job_hours"));
                    player.setJobExperience(result.getInt("job_xp"));
                }
                loaded = true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return loaded;
    }

    @Override
    public boolean update(LtrpPlayer player) {
        throw new NotImplementedException();
    }

    @Override
    public SpawnData getSpawnData(LtrpPlayer player) {
        SpawnData spawnData = null;
        String sql = "SELECT skin, spawn_type, spawn_ui FROM players WHERE id = ? LIMIT 1";
        System.out.println("SqlPlayerDaoImpl :: getSpawnData. Userid: " + player.getUserId());
        try(
                Connection connecetion = dataSource.getConnection();
                PreparedStatement stmt = connecetion.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                spawnData = new SpawnData();
                spawnData.setId(result.getInt("spawn_ui"));
                spawnData.setType(SpawnData.SpawnType.values()[result.getInt("spawn_type")]);
                spawnData.setSkin(result.getInt("skin"));
                System.out.println("SqlPlayerDaoImpl :: getSpawnData. Skin loaded:" + spawnData.getSkin());
                WeaponData[] weaponData = new WeaponData[3];
                for(int i = 0; i < weaponData.length; i++) {
                    weaponData[i] = new WeaponData(WeaponModel.NONE, 0);
                }
                spawnData.setWeaponData(weaponData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spawnData;
    }

    @Override
    public JailData getJailData(LtrpPlayer player) {
        JailData data = null;
        String sql = "SELECT * FROM player_jailtime WHERE player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUserId());
            ResultSet set = stmt.executeQuery();
            if(set.next()) {
                data = new JailData(set.getInt("id"), player,
                        JailData.JailType.valueOf(set.getString("type")),
                        set.getInt("remaining_time"),
                        set.getString("jailer"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public CrashData getCrashData(LtrpPlayer player) {
        CrashData crashData = null;
        String sql = "SELECT * FROM player_crashes WHERE player_id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                crashData = new CrashData(player,
                        new Location(
                                result.getFloat("pos_x"),
                                result.getFloat("pos_y"),
                                result.getFloat("pos_z"),
                                result.getInt("virtual_world"),
                                result.getInt("interior")
                        ), result.getDate("timestamp"));
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return crashData;
    }

    @Override
    public boolean remove(LtrpPlayer player, CrashData data) {
        String sql = "DELETE FROM player_crashes WHERE player_id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUserId());
            return stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean remove(LtrpPlayer player, JailData jailData) {
        String sql = "DELETE FROM player_jailtime WHERE player_id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUserId());
            return stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setFactionManager(LtrpPlayer player) {
        String sql = "UPDATE players SET faction_manager = 1 WHERE id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUserId());
            return stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void insert(JailData data) {
        String sql = "INSERT INTO player_jailtime (player_id, remaining_time, type, jailer_name, jail_date) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, data.getPlayer().getUserId());
            stmt.setInt(2, data.getTime());
            stmt.setString(3, data.getType().name());
            stmt.setString(4, data.getJailer());
            stmt.setObject(5, new Timestamp(new java.util.Date().getTime()));
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertCrime(PlayerCrime crime) {
        String sql = "INSERT INTO player_crimes (`name`, crime, reporter, `date`) VALUES(?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, crime.getPlayerName());
            stmt.setString(2, crime.getCrime());
            stmt.setString(3, crime.getReporterName());
            stmt.setDate(4, new Date(crime.getDate().getTime()));
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PlayerCrime> getCrimes(LtrpPlayer player) {
        List<PlayerCrime> crimes = new ArrayList<>();
        String sql = "SELECT * FROM player_crimes WHERE name = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setString(1, player.getName());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                crimes.add(new PlayerCrime(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("reporter"),
                        result.getString("crime"),
                        result.getDate("date"),
                        result.getInt("fine")
                ));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return crimes;
    }

    @Override
    public Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> getVehiclePermissions(LtrpPlayer player) {
        Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> permissions = new HashMap<>();
        String sql = "SELECT vehicle_id, permission FROM player_vehicle_permissions WHERE player_id = ?";
        int count = 1;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                int vehicleid = result.getInt("vehicle_id");
                String permission = result.getString("permission");

                if(permissions.containsKey(count)) {
                    if(permissions.get(count).getKey() == vehicleid) {
                        permissions.get(count).getValue().add(PlayerVehiclePermission.valueOf(permission));
                    } else {
                        count++;
                        Pair<Integer, List<PlayerVehiclePermission>> pair = new Pair<>(vehicleid, new ArrayList<>());
                        pair.getValue().add(PlayerVehiclePermission.valueOf(permission));
                        permissions.put(count, pair);
                    }
                } else {
                    count++;
                    Pair<Integer, List<PlayerVehiclePermission>> pair = new Pair<>(vehicleid, new ArrayList<>());
                    pair.getValue().add(PlayerVehiclePermission.valueOf(permission));
                    permissions.put(count, pair);
                }



                // If there are no vehicles in the slot [count] yet
                if(!permissions.containsKey(count)) {
                    // We add a new pair with an empty list to that slot
                    Pair<Integer, List<PlayerVehiclePermission>> pair = new Pair<>(vehicleid, new ArrayList<>());
                    permissions.put(count, pair);
                }
                // If the permission at [count] is for the same vehicleid result
                if(permissions.get(count).getKey() == vehicleid) {
                    permissions.get(count).getValue().add(PlayerVehiclePermission.valueOf(permission));
                } else {

                }


            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return permissions;
    }

    @Override
    public void updateLicenses(PlayerLicenses licenses) {
        for(PlayerLicense license : licenses.get()) {
            if(license != null) {
                updateLicense(license);
            }
        }
    }

    @Override
    public void updateLicense(PlayerLicense license) {
        String sql = "UPDATE player_licenses SET type = ?, stage = ?, `date` = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setString(1, license.getType().name());
            stmt.setInt(2, license.getStage());
            stmt.setTimestamp(3,license.getDateAquired());
            stmt.setInt(4, license.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertLicense(PlayerLicense license) {
        String sql = "INSERT INTO player_licenses (player_id, type, stage, `date`) VALUES(?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, license.getPlayer().getUserId());
            stmt.setString(2, license.getType().name());
            stmt.setInt(3, license.getStage());
            stmt.setTimestamp(4,license.getDateAquired());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()) {
                license.setId(keys.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerLicenses get(LtrpPlayer player) {
        PlayerLicenses licenses = new PlayerLicenses(player);
        String sql = "SELECT * FROM player_licenses WHERE player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                PlayerLicense license = new PlayerLicense();
                license.setPlayer(player);
                license.setId(result.getInt("id"));
                license.setType(LicenseType.valueOf(result.getString("type")));
                license.setStage(result.getInt("stage"));
                license.setDateAquired(result.getTimestamp("date"));
                license.setWarnings(getWarnings(license));
                licenses.add(license);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return licenses;
    }

    @Override
    public void delete(PlayerLicense license) {
        String sql = "DELETE FROM player_licenses WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, license.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public LicenseWarning[] getWarnings(PlayerLicense license) {
        String sql = "SELECT * FROM player_license_warnings WHERE license_id = ?";
        List<LicenseWarning> warnings = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, license.getId());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                LicenseWarning warning = new LicenseWarning();
                warning.setId(result.getInt("id"));
                warning.setBody(result.getString("body"));
                warning.setDate(result.getDate("date"));
                warning.setIssuedBy(result.getString("issued_by"));
                warning.setLicense(license);
                warnings.add(warning);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return warnings.toArray(new LicenseWarning[0]);
    }

    public void insertWarning(LicenseWarning warning) {

    }

}
