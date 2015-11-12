package lt.ltrp.dao.impl;

import lt.ltrp.dao.PlayerDao;
import lt.ltrp.player.CrashData;
import lt.ltrp.player.JailData;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.SpawnData;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Player;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        int userid = LtrpPlayer.INVALID_USER_ID;
        String sql = "SELECT id FROM players WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ) {
            stmt.setString(1, player.getName());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
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
        return false;
    }

    @Override
    public SpawnData getSpawnData(LtrpPlayer player) {
        SpawnData spawnData = null;
        String sql = "SELECT skin, spawn_type, spawn_ui FROM players WHERE id = ? LIMIT 1";
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
                spawnData.setWeaponData(new WeaponData[3]);
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
                data = new JailData(JailData.JailType.valueOf(set.getString("type")),
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
}
