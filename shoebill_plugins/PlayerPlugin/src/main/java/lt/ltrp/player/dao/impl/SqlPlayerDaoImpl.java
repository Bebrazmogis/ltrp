package lt.ltrp.player.dao.impl;


import lt.ltrp.constant.TalkStyle;
import lt.ltrp.constant.WalkStyle;
import lt.ltrp.data.*;
import lt.ltrp.object.Entity;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.object.PlayerData;
import lt.ltrp.player.object.impl.PlayerDataImpl;
import lt.ltrp.player.dao.PlayerDao;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.util.event.EventManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;


/**
 * @author Bebras
 *         2015.11.12.
 */

public class SqlPlayerDaoImpl implements PlayerDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlPlayerDaoImpl.class);

    private DataSource dataSource;
    private EventManager eventManager;

    public SqlPlayerDaoImpl(DataSource ds, EventManager eventManager) {
        if(ds == null)
            throw new IllegalArgumentException("Datasource cannot be null");
        this.dataSource = ds;
        this.eventManager = eventManager;
    }


    public int getUserId(Player player) {
        logger.info("SqlPlayerDaoImpl :: getUserId for player " + player.getName());
        int userid = Entity.Companion.getINVALID_ID();
        logger.debug("Thats it :/");
        String sql = "SELECT id FROM players WHERE username = ?";
        logger.debug("Creating connection");
        try (Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ) {
            logger.debug("Formatting statement");
            stmt.setString(1, player.getName());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                logger.debug("SqlPlayerDaoImpl :: getUserId userid is " + result.getInt("id"));
                userid = result.getInt("id");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return userid;
    }

    public String getPassword(LtrpPlayer player) {
        String password = null;
        String sql = "SELECT `password` FROM players WHERE id = ? LIMIT 1";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                password = result.getString("password");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return password;
    }

    public boolean loadData(LtrpPlayer player) {
        boolean loaded = false;
        String sql = "SELECT " +
                "secret_question, secret_answer, admin_level, mod_level, players.level, players.job_id, money, hours_online, box_style, age, " +
                "respect, deaths, hunger, total_paycheck, current_paycheck, minutes_online_since_payday, forum_name, ucp_user_id " +
                "last_login, description, nationality, sex, walk_style, talk_style " +
                "FROM players " +
                " WHERE players.id = ? LIMIT 1";
        /*String sql = "SELECT " +
                "secret_question, secret_answer, admin_level, mod_level, players.level, players.job_id, money, hours_online, box_style, age, " +
                "respect, deaths, hunger, total_paycheck, job_contract, minutes_online_since_payday, forum_name " +
                ", player_job_levels.level AS job_level, player_job_levels.hours AS job_hours, player_job_levels.xp AS job_xp " +
                "FROM players LEFT JOIN player_job_levels ON players.id = player_job_levels.player_id AND players.job_id = player_job_levels.job_id" +
                " WHERE players.id = ? LIMIT 1";*/
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                player.setAdminLevel(result.getInt("admin_level"));
                player.setLevel(result.getInt("level"));
                player.setMoney(result.getInt("money"));
                //player.setJob(Job.get(result.getInt("job_id")));
                player.setSecretQuestion(result.getString("secret_question"));
                player.setSecretAnswer(result.getString("secret_answer"));
                player.setOnlineHours(result.getInt("hours_online"));
                player.setBoxingStyle(result.getInt("box_style"));
                player.setAge(result.getInt("age"));
                //player.setRespect(result.getInt("respect"));
                player.setDeaths(result.getInt("deaths"));
                player.setHunger(result.getInt("hunger"));
                player.setTotalPaycheck(result.getInt("total_paycheck"));
                player.setCurrentPaycheck(result.getInt("current_paycheck"));
               // player.setJobContract(result.getInt("job_contract"));
                player.setMinutesOnlineSincePayday(result.getInt("minutes_online_since_payday"));
                player.setModLevel(result.getInt("mod_level"));
                player.setForumName(result.getString("forum_name"));
              //  player.setUcpId(result.getInt("ucp_user_id"));
                player.setLastLogin(result.getTimestamp("last_login").toLocalDateTime());
                player.setDescription(result.getString("description"));
                player.setOrigin(result.getString("nationality"));
                player.setSex(result.getString("sex"));
                player.setWalkStyle(WalkStyle.getById(result.getInt("walk_style")));
                player.setTalkStyle(TalkStyle.getById(result.getInt("talk_style")));

                /*int jobLevel = result.getInt("job_level");
                if(!result.wasNull()) {
                   // player.setJobLevel(jobLevel);
                    player.setJobHours(result.getInt("job_hours"));
                    player.setJobExperience(result.getInt("job_xp"));
                }*/
                loadSettings(player, connection);
                loaded = true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return loaded;
    }

    private void loadSettings(LtrpPlayer player, Connection connection) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement("SELECT * FROM player_settings WHERE player_id = ?")) {
            stmt.setInt(1, player.getUUID());
            ResultSet r = stmt.executeQuery();
            Properties settings = new Properties();
            while(r.next()) {
                settings.put(r.getString("setting"), r.getString("value"));
            }
            // ERROR tODO
            //player.setSettings(new PlayerSettings(player, settings));
        }
    }

    public boolean update(LtrpPlayer player) {
        throw new NotImplementedException();
    }
/*
    @Override
    public SpawnData getSpawnData(LtrpPlayer player) {
        SpawnData spawnData = null;
        String sql = "SELECT skin, spawn_type, spawn_ui FROM players WHERE id = ? LIMIT 1";
        System.out.println("SqlPlayerDaoImpl :: getSpawnData. Userid: " + player.getUUID());
        try(
                Connection connecetion = dataSource.getConnection();
                PreparedStatement stmt = connecetion.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
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
    public void update(LtrpPlayer player, SpawnData spawnData) {
        update(player.getUUID(), spawnData);
    }

    @Override
    public void update(int i, SpawnData spawnData) {
        String sql = "UPDATE players SET skin = ?, spawn_type = ?, spawn_ui = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, spawnData.getSkin());
            stmt.setInt(2, spawnData.getType().ordinal());
            stmt.setInt(3, spawnData.getId());
            stmt.setInt(4, i);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    */
/*
    @Override
    public void setSpawnData(LtrpPlayer player) {
        String sql = "UPDATE players SET skin = ?, spawn_type = ?, spawn_ui = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            SpawnData spawnData = player.getSpawnData();
            stmt.setInt(1, spawnData.getSkin());
            stmt.setInt(2, spawnData.getType().ordinal());
            stmt.setInt(3, spawnData.getId());
            stmt.setInt(4, player.getUUID());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
*/
    /*
    @Override
    public JailData getJailData(LtrpPlayer player) {
        JailData data = null;
        String sql = "SELECT * FROM player_jailtime WHERE player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
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
*/
    public CrashData getCrashData(LtrpPlayer player) {
        throw new NotImplementedException();
        /*CrashData crashData = null;
        String sql = "SELECT * FROM player_crashes WHERE player_id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
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
        return crashData;*/
    }

    public boolean remove(LtrpPlayer player, CrashData data) {
        String sql = "DELETE FROM player_crashes WHERE player_id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
            return stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
/*
    @Override
    public boolean remove(LtrpPlayer player, JailData jailData) {
        String sql = "DELETE FROM player_jailtime WHERE player_id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
            return stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
*/
    public boolean setFactionManager(LtrpPlayer player) {
        String sql = "UPDATE players SET faction_manager = 1 WHERE id = ?";
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
            return stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
/*
    @Override
    public void insert(JailData data) {
        String sql = "INSERT INTO player_jailtime (player_id, remaining_time, type, jailer_name, jail_date) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, data.getPlayer().getUUID());
            stmt.setInt(2, data.getTime());
            stmt.setString(3, data.getType().name());
            stmt.setString(4, data.getJailer());
            stmt.setObject(5, new Timestamp(new java.util.Date().getTime()));
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
*/
/*
    public void insertCrime(PlayerFine crime) {
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


    public List<PlayerFine> getCrimes(LtrpPlayer player) {
        List<PlayerFine> crimes = new ArrayList<>();
        String sql = "SELECT * FROM player_crimes WHERE name = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setString(1, player.getName());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                crimes.add(new PlayerFine(
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
    }*/
/*
    //@Override
    public Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> getVehiclePermissions(LtrpPlayer player) {
        Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> permissions = new HashMap<>();
        String sql = "SELECT vehicle_id, permission FROM player_vehicle_permissions WHERE player_id = ?";
        int count = 1;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
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
*/

/*
    public void updateLicenses(PlayerLicenses licenses) {
        for(PlayerLicense license : licenses.get()) {
            if(license != null) {
                updateLicense(license);
            }
        }
    }

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

    public void insertLicense(PlayerLicense license) {
        String sql = "INSERT INTO player_licenses (player_id, type, stage, `date`) VALUES(?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, license.getPlayer().getUUID());
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

    public PlayerLicenses get(LtrpPlayer player) {
        PlayerLicenses licenses = new PlayerLicenses(player);
        String sql = "SELECT * FROM player_licenses WHERE player_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
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
*/

    public void removeJob(int userId) {
        String sql = "UPDATE players SET job_id = NULL, job_rank = NULL WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUsername(int userId) {
        String sql = "SELECT username FROM players WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserId(String username) {
        String sql = "SELECT id FROM players WHERE username= ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return Entity.Companion.getINVALID_ID();
    }

    public void updateLastLogin(LtrpPlayer ltrpPlayer) {
        String sql = "UPDATE players SET last_login = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setTimestamp(1, Timestamp.valueOf(ltrpPlayer.getLastLogin()));
            stmt.setInt(2, ltrpPlayer.getUUID());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
/*
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
                warning.setBody(result.getString("warning"));
                warning.setDate(result.getTimestamp("date"));
                warning.setIssuedBy(result.getString("issued_by"));
                warning.setLicense(license);
                warnings.add(warning);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return warnings.toArray(new LicenseWarning[0]);
    }

    public void insert(LicenseWarning warning) {
        String sql = "INSERT INTO player_license_warnings (license_id, warning, issued_by, `date`) VALUES (?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, warning.getLicense().getId());
            stmt.setString(2, warning.getBody());
            stmt.setString(3, warning.getIssuedBy());
            stmt.setTimestamp(4, warning.getDate());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next())
                warning.setId(keys.getInt(1));
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
*/
    @Override
    public PlayerData get(@NotNull String name) {
        String sql = "SELECT * FROM players WHERE username = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setString(1, name);
            ResultSet r = stmt.executeQuery();
            if(r.next())
                return toData(r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    @Override
    public PlayerData get(int uuid) {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, uuid);
            ResultSet r = stmt.executeQuery();
            return toData(r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid user id " + uuid);
    }

    @Override
    public void update(@NotNull PlayerData playerData) {
        String sql = "UPDATE players SET " +
                "username = ?," +
                "password = ?," +
                "secret_question = ?," +
                "secret_answer = ?," +
                "level = ?," +
                "admin_level = ?," +
                "mod_level = ?," +
                "hours_online = ?," +
                "minutes_online_since_payday = ?," +
                "box_style = ?," +
                "sex = ?," +
                "age = ?," +
                "origin = ?," +
                "disease = ?," +
                "respect = ?," +
                "money = ?," +
                "deaths = ?," +
                "wanted_level = ?," +
                "walk_style = ?," +
                "talk_style = ?," +
                "last_login = ? ," +
                "hunger = ?," +
                "total_paycheck = ? " +
                "WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, playerData.getName());
            stmt.setString(2, playerData.getPassword());
            stmt.setString(3, playerData.getSecretQuestion());
            stmt.setString(4, playerData.getSecretAnswer());
            stmt.setInt(5, playerData.getLevel());
            stmt.setInt(6, playerData.getAdminLevel());
            stmt.setInt(7, playerData. getModLevel());
            stmt.setInt(8, playerData.getHoursOnline());
            stmt.setInt(9, playerData.getMinutesOnlineSincePayday());
            stmt.setInt(10, playerData.getBoxStyle());
            stmt.setString(11, playerData.getSex());
            stmt.setInt(12, playerData.getAge());
            stmt.setString(13, playerData.getOrigin());
            //stmt.setInt(14, playerData.getDisease());
           // stmt.setInt(15, playerData.getRespect());
            stmt.setInt(16, playerData.getMoney());
            stmt.setInt(17, playerData.getDeaths());
           // stmt.setInt(18, playerData.getWantedLevel());
            stmt.setInt(19, playerData.getWalkStyle().getId());
            stmt.setInt(20, playerData.getTalkStyle().getId());
            stmt.setTimestamp(21, Timestamp.valueOf(playerData.getLastLogin()));
            stmt.setInt(22, playerData.getHunger());
            stmt.setInt(23, playerData.getTotalPaycheck());
            stmt.setInt(24, playerData.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PlayerDataImpl toData(ResultSet r) throws SQLException {
        /*PlayerDataImpl impl =  new PlayerDataImpl(r.getInt("id"), r.getString("username"),
                r.getString("password"),
                r.getString("secret_question"),
                r.getString("secret_answer"),
                r.getInt("level"),
                r.getInt("admin_level"),
                r.getInt("mod_level"),
                r.getInt("hours_online"),
                r.getInt("minutes_online_since_payday"),
                r.getInt("box_style"),
                r.getString("sex"),
                r.getInt("age"),
                r.getString("origin"),
                r.getInt("disease"),
                r.getInt("respect"),
                r.getInt("money"),
                r.getInt("deaths"),
                r.getInt("wanted_level"),
                WalkStyle.getById(r.getInt("walk_style")),
                TalkStyle.getById(r.getInt("talk_style")),
                r.getTimestamp("last_login").toLocalDateTime(),
                r.getInt("hunger"),
                r.getInt("total_paycheck"),
                null,
                eventManager);
                */
        //impl.setJobData(PlayerJobController.Companion.getInstance().getData(impl));
        throw new NotImplementedException();
    }
}
