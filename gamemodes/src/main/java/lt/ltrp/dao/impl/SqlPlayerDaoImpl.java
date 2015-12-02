package lt.ltrp.dao.impl;

import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.Animation;
import lt.ltrp.item.*;
import lt.ltrp.player.CrashData;
import lt.ltrp.player.JailData;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.SpawnData;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Player;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        String sql = "SELECT admin_level, level FROM players WHERE id = ? LIMIT 1";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                player.setAdminLevel(result.getInt("admin_level"));
                player.setLevel(result.getInt("level"));

                loaded = true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return loaded;
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
    public Item[] getItems(LtrpPlayer player) {
        String sql = "SELECT * FROM player_items WHERE player_id = ?";
        List<Item> items = new ArrayList<>();
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUserId());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                Item item;
                int id = result.getInt("id");
                String name = result.getString("name");
                String className = result.getString("class_name");
                ItemType type = ItemType.getById(result.getInt("type"));

                // A.K.A. class == lt.ltrp.item.MeleeWeaponItem
                if(type == ItemType.Wrench || type == ItemType.Crowbar || type == ItemType.Hammer || type == ItemType.Flashlight || type == ItemType.Tazer || type == ItemType.Screwdriver) {
                    item = new MeleeWeaponItem(name, id, type, result.getInt("model_id"), PlayerAttachBone.values()[result.getInt("bone")-1], new Animation(result.getString("anim_lib"), result.getString("anim_name"), false, 1000), result.getFloat("dmg_increase"));
                } else if(type == ItemType.FishingRod) {
                    item = new FishingRodItem(name, id);
                }

                switch(className) {
                    case "lt.ltrp.item.ItemPhone":
                        item = new ItemPhone(name, id, result.getInt("phonenumber"));
                        break;
                    case "lt.ltrp.item.DrinkItem":
                        item = new DrinkItem(name, id, type, result.getInt("doses_left"), SpecialAction.DRINK_BEER);
                        break;
                    case "lt.ltrp.item.MaskItem":
                        item = new MaskItem(name, id, type, result.getInt("model_id"), PlayerAttachBone.HEAD);
                        break;
                    case "lt.ltrp.item.ClothingItem":
                        item = new ClothingItem(name, id, type, result.getInt("model_id"), PlayerAttachBone.values()[result.getInt("bone")-1]);
                        break;
                    case "lt.ltrp.item.WeaponItem":
                        int weaponModelId = result.getInt("weapon_model");
                        WeaponModel wepmodel = WeaponModel.get(weaponModelId);
                        item = new WeaponItem(new WeaponData(wepmodel, result.getInt("ammo")), name, id, type);
                        break;
                    case "lt.ltrp.item.DiceItem":
                        item = new DiceItem(id);
                        break;
                    default:
                        item = new BasicItem(name, id, type);
                        break;
                }
                int durability = result.getInt("durability");
                if(!result.wasNull()) {
                    if(className.equals("lt.ltrp.item.CigarettesItem")) {
                        item = new CigarettesItem(name, id, durability);
                    } else {
                        item = new DurableItem(name, id, type, durability, result.getInt("max_durability"));
                    }
                }

                int itemCount = result.getInt("items");
                if(!result.wasNull()) {
                    int size = result.getInt("size");
                    switch(className) {
                        case "lt.ltrp.FuelTankItem":
                            item = new FuelTankItem(name, id, itemCount, size);
                            break;
                        default:
                            item = new ContainerItem(name, id, type, itemCount, size);
                            break;
                    }
                }



                items.add(item);
            }
        } catch(SQLException e) {
            e.printStackTrace();

        }
        return items.toArray(new Item[0]);
    }

    @Override
    public void updateItem(LtrpPlayer player, Item item) {

    }

    @Override
    public void addItem(LtrpPlayer player, Item item) {

    }



}
