package lt.ltrp.player.dao.impl;

import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.dao.PlayerWeaponDao;
import net.gtaun.shoebill.constant.WeaponModel;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class MySqlPlayerWeaponDaoImpl implements PlayerWeaponDao {

    private DataSource dataSource;

    public MySqlPlayerWeaponDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public LtrpWeaponData[] get() {
        ArrayList<LtrpWeaponData> data = new ArrayList<>();
        String sql = "SELECT * FROM player_wielded_weapons WHERE deleted_at IS NULL";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                data.add(new LtrpWeaponData(r.getInt("id"), WeaponModel.get(r.getInt("weapon_id")), r.getInt("ammo"), false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data.toArray(new LtrpWeaponData[0]);
    }

    @Override
    public int insert(LtrpPlayer player, LtrpWeaponData weapon) {
        String sql = "INSERT INTO player_wielded_weapons (player_id, weapon_id, ammo, created_at) VALUES (?, ?, ?, ?)";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            stmt.setInt(1, player.getUUID());
            stmt.setInt(2, weapon.getWeaponData().model.id);
            stmt.setInt(3, weapon.getWeaponData().ammo);
            stmt.setTimestamp(4, new Timestamp(Instant.now().getEpochSecond()));
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next())
                return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void remove(LtrpWeaponData weaponData) {
        String sql = "UPDATE player_wielded_weapons SET deleted_at = ? WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, weaponData.getUUID());
            stmt.setTimestamp(2, new Timestamp(Instant.now().getEpochSecond()));
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LtrpWeaponData[] get(LtrpPlayer player) {
        ArrayList<LtrpWeaponData> data = new ArrayList<>();
        String sql = "SELECT * FROM player_wielded_weapons WHERE player_id = ? AND deleted_at IS NULL";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                data.add(new LtrpWeaponData(r.getInt("id"), WeaponModel.get(r.getInt("weapon_id")), r.getInt("ammo"), false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data.toArray(new LtrpWeaponData[0]);
    }

    @Override
    public void update(LtrpWeaponData weaponData) {
        String sql = "UPDATE player_wielded_weapons SET weapon_id = ?, ammo = ? WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, weaponData.getWeaponData().model.id);
            stmt.setInt(2, weaponData.getWeaponData().ammo);
            stmt.setInt(3, weaponData.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
