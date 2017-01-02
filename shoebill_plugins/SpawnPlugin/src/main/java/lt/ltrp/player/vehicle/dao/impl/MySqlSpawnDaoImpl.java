package lt.ltrp.player.vehicle.dao.impl;

import lt.ltrp.dao.SpawnDao;
import lt.ltrp.spawn.data.SpawnData;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.WeaponData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class MySqlSpawnDaoImpl implements SpawnDao {

    private DataSource dataSource;

    public MySqlSpawnDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public SpawnData get(int userId) {
        SpawnData spawnData = null;
        String sql = "SELECT skin, spawn_type, spawn_ui FROM players WHERE id = ? LIMIT 1";
        try(
                Connection connecetion = dataSource.getConnection();
                PreparedStatement stmt = connecetion.prepareStatement(sql);
        ) {
            stmt.setInt(1,userId);
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
    public SpawnData get(LtrpPlayer player) {
        return get(player.getUUID());
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

    @Override
    public void update(LtrpPlayer player, SpawnData spawnData) {
        update(player.getUUID(), spawnData);
    }
}
