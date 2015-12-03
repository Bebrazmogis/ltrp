package lt.ltrp.dao.impl;

import lt.ltrp.dao.ItemDao;
import lt.ltrp.data.Animation;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.item.*;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.WeaponData;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class SqlItemDao implements ItemDao {

    private DataSource dataSource;

    public SqlItemDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int obtainItemId(String name, String classname, ItemType type, Class locationType, int locationid) {
        int itemid = 0;
        String sql = "INSERT INTO items (`name`, class_name, location_class, location_id) VALUES(?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            stmt.setString(1, name);
            stmt.setString(2, classname);
            stmt.setString(3, locationType.getName());
            stmt.setInt(4, locationid);
            ResultSet keys = stmt.executeQuery();
            if(keys.next()) {
                itemid = keys.getInt(1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return itemid;
    }

    @Override
    public Item[] getItems(Class locationType, int locationid) {
        String sql = "SELECT * FROM items WHERE location_class = ? AND location_id = ?";
        List<Item> items = new ArrayList<>();
        try(
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, locationType.getName());
            stmt.setInt(2, locationid);
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                items.add(resultSetToItem(result));
            }
        } catch(SQLException e) {
            e.printStackTrace();

        }
        return items.toArray(new Item[0]);
    }

    @Override
    public void updateItem(Item item) {

    }

    @Override
    public void updateItem(Item item, Class locationType, int locationid) {

    }

    private Item resultSetToItem(ResultSet result) throws SQLException {
        Item item = null;
        int id = result.getInt("id");
        boolean stackable = result.getInt("stackable") == 1;
        String name = result.getString("name");
        String className = result.getString("class_name");
        ItemType type = ItemType.getById(result.getInt("type"));

        // A.K.A. class == lt.ltrp.item.MeleeWeaponItem
        if(type == ItemType.Wrench || type == ItemType.Crowbar || type == ItemType.Hammer || type == ItemType.Flashlight || type == ItemType.Tazer || type == ItemType.Screwdriver) {
            item = new MeleeWeaponItem(name, id, type, result.getInt("model_id"), PlayerAttachBone.values()[result.getInt("bone")-1], new Animation(result.getString("anim_lib"), result.getString("anim_name"), false, 1000), result.getFloat("dmg_increase"));
        } else if(type == ItemType.FishingRod) {
            item = new FishingRodItem(name, id);
        } else if(type == ItemType.Amphetamine) {
            item = new AmphetamineItem(name, id, ItemType.Amphetamine, result.getInt("doses_left"));
        } else if(type == ItemType.Cocaine) {
            item = new CocaineItem(name, id, result.getInt("doses_left"));
        } else if(type == ItemType.Extazy) {
            item = new EctazyItem(name, id, result.getInt("doses_left"));
        } else if(type == ItemType.Heroin) {
            item = new HeroinItem(name, id, result.getInt("doses_left"));
        } else if(type == ItemType.MetaAmphetamine) {
            item = new MetaamphetamineItem(name, id, result.getInt("doses_left"));
        } else if(type == ItemType.Molotov) {
            item = new MolotovItem(name, id);
        } else if(type == ItemType.Pcp) {
            item = new PcpItem(name, id, result.getInt("doses_left"));
        } else if(type == ItemType.WeedSeed) {
            item = new WeedSeedItem(name, id);
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
                item = new WeaponItem(new LtrpWeaponData(wepmodel, result.getInt("ammo"), false), name, id, type);
                break;
            case "lt.ltrp.item.DiceItem":
                item = new DiceItem(id);
                break;
            default:
                item = new BasicItem(name, id, type, stackable);
                break;
        }
        int durability = result.getInt("durability");
        if(!result.wasNull()) {
            if(className.equals("lt.ltrp.item.CigarettesItem")) {
                item = new CigarettesItem(name, id, durability);
            } else {
                item = new DurableItem(name, id, type, durability, result.getInt("max_durability"), stackable);
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
                    item = new ContainerItem(name, id, type, stackable, itemCount, size);
                    break;
            }
        }
        return item;
    }
}
