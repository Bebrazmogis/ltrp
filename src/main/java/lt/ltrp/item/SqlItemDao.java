package lt.ltrp.item;

import lt.ltrp.dao.ItemDao;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.03.
 */

public class SqlItemDao implements ItemDao {

    private DataSource dataSource;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SqlItemDao.class);

    public SqlItemDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Item[] getItems(Class locationType, int locationid) {
        logger.debug("getItems called locationType=" + locationType + " locationid=" + locationid);
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE location = ? AND location_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setString(1, locationType.getName());
            stmt.setInt(2, locationid);
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                int itemid = result.getInt("item_id");
                ItemType type = ItemType.getById(result.getInt("type"));
                String typeClass = result.getString("type_class");
                AbstractItem item = null;
                try {
                    Class<?> cls = Class.forName(typeClass);
                    Method method = cls.getDeclaredMethod("getById", Integer.TYPE, ItemType.class, Connection.class);
                    if(!method.isAccessible()) {
                        logger.error(method.getName() + " is not accessible");
                        method.setAccessible(true);
                    }
                    item = (AbstractItem)method.invoke(null, itemid, type, con);

                } catch (ClassNotFoundException e) {
                    logger.error("Class " + typeClass + " not found. Cause:" + e.getMessage());
                } catch(NoSuchMethodException e) {
                    logger.error("Method getById could not be found. Cause: " +e.getMessage());
                } catch(IllegalAccessException e) {
                    logger.error("Method getById can not be accessed. Cause" + e.getMessage());
                } catch(InvocationTargetException e) {
                    logger.error("Invocation target exception. Cause: "+ e.getMessage());
                }/*
                switch(typeClass) {
                    case "lt.ltrp.item.DurableItem":
                        item = DurableItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.CigarettesItem":
                        item = CigarettesItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.MolotovItem":
                        item = MolotovItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.ContainerItem":
                        item = ContainerItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.FuelTankItem":
                        item = FuelTankItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.DiceItem":
                        item = DiceItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.WeedSeedItem":
                        item = WeedSeedItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.HouseAudioItem":
                        item = HouseAudioItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.ClothingItem":
                        item = ClothingItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.MeleeWeaponItem":
                        item = MeleeWeaponItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.MaskItem":
                        item = MaskItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.FishingRodItem":
                        item = FishingRodItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.SuitcaseItem":
                        item = SuitcaseItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.WeaponItem":
                        item = WeaponItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.DrugItem":
                        item = DrugItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.EctazyItem":
                        item = EctazyItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.AmphetamineItem":
                        item = AmphetamineItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.CocaineItem":
                        item = CocaineItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.PcpItem":
                        item = PcpItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.MetaamphetamineItem":
                        item = MetaamphetamineItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.HeroinItem":
                        item = HeroinItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.DrinkItem":
                        item = DrinkItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.ItemPhone":
                        item = ItemPhone.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.WeedItem":
                        item = WeedItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.RadioItem":
                        item = RadioItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.Toolbox":
                        item = ToolboxItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.Mp3Item":
                        item = Mp3Item.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.BoomBoxItem":
                        item = BoomBoxItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.BasicItem":
                        item = BasicItem.getById(itemid, type, con);
                        break;

                }
                */
                if(item != null) {
                    items.add(item);
                    item.setGlobalId(result.getInt("id"));
                }
                else
                    logger.error("Item " + result.getInt("id") + " implementation not found. Type class:" + typeClass + " type id: "+ type.id);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return items.toArray(new Item[0]);
    }

    @Override
    public void insert(Item item, Class location, int locationid) {
        logger.debug("insert called. Item id=" + item.getGlobalId() + " location=" + location + " locationid=" + locationid);
        AbstractItem abstractItem = (AbstractItem)item;
        String sql = "INSERT INTO items (type, type_class, location, location_id, item_id) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement globalStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            int itemid = abstractItem.insert(con);
            abstractItem.setItemId(itemid);

            globalStatement.setInt(1, abstractItem.getType().id);
            globalStatement.setString(2, item.getClass().getName());
            globalStatement.setString(3, location.getName());
            globalStatement.setInt(4, locationid);
            globalStatement.setInt(5, abstractItem.getItemId());
            globalStatement.executeUpdate();
            ResultSet result = globalStatement.getGeneratedKeys();
            if(result.next()) {
                abstractItem.setGlobalId(result.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete(Item item) {
        logger.debug("delete called globalidid=" + item.getGlobalId() + "itemid=" + item.getItemId());
        AbstractItem abstractItem = (AbstractItem)item;
        String sql = "DELETE FROM items WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            abstractItem.delete(connection);
            stmt.setInt(1, item.getGlobalId());
            stmt.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Item item) {
        logger.debug("update called item id=" + item.getGlobalId());
        AbstractItem abstractItem = (AbstractItem)item;
        try (
                Connection connection = dataSource.getConnection();
        ) {
            this.update(abstractItem, connection);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(AbstractItem item, Connection con) throws SQLException {
        item.update(con);
    }

    @Override
    public void update(Item item, Class location, int locationid) {
        logger.debug("update called item id=" + item.getGlobalId() + " location=" + location + " locationid=" + locationid);
        new Thread(() -> {
            String sql = "UPDATE items SET location = ?, location_id = ? WHERE id = ?";
            try (
                    Connection con = dataSource.getConnection();
                    PreparedStatement stmt = con.prepareStatement(sql);
                ) {
                this.update((AbstractItem)item, con);
                stmt.setString(1, location.getName());
                stmt.setInt(2, locationid);
                stmt.setInt(3, item.getGlobalId());
                stmt.execute();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public int generatePhonenumber() {
        logger.debug("generatePhonenumber called");
        try (
                Connection con = dataSource.getConnection();
                ) {
                return ItemPhone.generateNumber(con);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

/*
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



    public void updateItem2(Item item) {
        String sql = "SELECT item_id FROM items WHERE id = ?";

    }

    @Override
    public void updateItem(Item item) {
        PreparedStatement stmt = null;
        try (
               Connection con = dataSource.getConnection();
               ) {
            if(item instanceof  BasicItem)
                stmt = getStatement((BasicItem)item, con);
            else if(item instanceof ConsumableItem)
                stmt = getStatement((ConsumableItem)item, con);

        } catch(SQLException e) {
           e.printStackTrace();
        }
    }



    private PreparedStatement getStatement(BasicItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getId());
        return stmt;
    }

    private PreparedStatement getStatement(ConsumableItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, doses_left = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getDosesLeft());
        stmt.setInt(4, item.getId());
        return stmt;
    }

    private PreparedStatement getStatement(ContainerItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, items = ?, size = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getItemCount());
        stmt.setInt(4, item.getSize());
        stmt.setInt(5, item.getId());
        return stmt;
    }

    private PreparedStatement getStatement(DurableItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, durability = ?, max_durability = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getDurability());
        stmt.setInt(4, item.getMaxDurability());
        stmt.setInt(5, item.getId());
        return stmt;
    }

    private PreparedStatement getStatement(ClothingItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, model_id = ?, bone = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getModelid());
        stmt.setInt(4, item.getBone().getValue());
        stmt.setInt(5, item.getId());
        return stmt;
    }

    private PreparedStatement getStatement(MeleeWeaponItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, model_id = ?, bone = ?, anim_lib = ?, anim_name = ?, dmg_increase = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getModelid());
        stmt.setInt(4, item.getBone().getValue());
        stmt.setString(5, item.getAnimation().getAnimLib());
        stmt.setString(6, item.getAnimation().getAnimName());
        stmt.setFloat(7, item.getDamageIncrease());
        stmt.setInt(8, item.getId());
        return stmt;
    }

    private PreparedStatement getStatement(WeaponItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, weapon_model = ?, ammo = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getWeaponData().getModel().getId());
        stmt.setInt(4, item.getWeaponData().getAmmo());
        stmt.setInt(5, item.getId());
        return stmt;
    }


    private PreparedStatement getStatement(DrinkItem item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, special_action = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getSpecialAction().getValue());
        stmt.setInt(4, item.getId());
        return stmt;
    }

    private PreparedStatement getStatement(ItemPhone item, Connection con) throws SQLException {
        String sql = "UPDATE items SET stackable = ?, amount = ?, phonenumber = ? WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, item.isStackable() ? 1 : 0);
        stmt.setInt(2, item.getAmount());
        stmt.setInt(3, item.getPhonenumber());
        stmt.setInt(4, item.getId());
        return stmt;
    }




    @Override
    public void updateItem(Item item, Class locationType, int locationid) {
        String sql = "UPDATE items SET location_class = ? AND location_id = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, locationType.getName());
            stmt.setInt(2, locationid);
            stmt.setInt(3, item.getId());
        } catch(SQLException e) {
            e.printStackTrace();
        }
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
    */
}
