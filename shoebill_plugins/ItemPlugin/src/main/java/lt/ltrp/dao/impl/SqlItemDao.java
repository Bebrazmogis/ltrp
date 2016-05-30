package lt.ltrp.dao.impl;


import lt.ltrp.constant.ItemType;
import lt.ltrp.dao.ItemDao;
import lt.ltrp.dao.PhoneDao;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.object.ClothingItem;
import lt.ltrp.object.*;
import lt.ltrp.object.ContainerItem;
import lt.ltrp.object.impl.*;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;

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
    private EventManager eventManager;
    private PhoneDao phoneDao;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SqlItemDao.class);

    public SqlItemDao(DataSource dataSource, EventManager eventManager, PhoneDao phoneDao) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
        this.phoneDao = phoneDao;
    }

/*
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
                }
                switch(typeClass) {
                    case "lt.ltrp.item.DurableItem":
                        item = DurableItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.CigarettesItem":
                        item = CigarettesItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.MolotovItem":
                        item = MolotovItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.ContainerItem":
                        item = ContainerItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.FuelTankItem":
                        item = FuelTankItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.DiceItem":
                        item = DiceItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.WeedSeedItem":
                        item = WeedSeedItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.HouseAudioItem":
                        item = HouseAudioItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.ClothingItem":
                        item = ClothingItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.MeleeWeaponItem":
                        item = MeleeWeaponItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.MaskItem":
                        item = MaskItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.FishingRodItem":
                        item = FishingRodItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.SuitcaseItem":
                        item = SuitcaseItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.WeaponItem":
                        item = WeaponItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.drug.DrugItem":
                        item = DrugItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.EctazyItem":
                        item = EctazyItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.AmphetamineItem":
                        item = AmphetamineItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.CocaineItem":
                        item = CocaineItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.PcpItem":
                        item = PcpItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.MetaamphetamineItem":
                        item = MetaamphetamineItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.HeroinItem":
                        item = HeroinItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.DrinkItem":
                        item = DrinkItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.ItemPhone":
                        item = ItemPhone.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.WeedItem":
                        item = WeedItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.RadioItem":
                        item = RadioItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.item.Toolbox":
                        item = ToolboxItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.Mp3Item":
                        item = Mp3Item.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.BoomBoxItem":
                        item = BoomBoxItem.getById(itemid, type, con);
                        break;
                    case "lt.ltrp.object.impl.BasicItem":
                        item = BasicItem.getById(itemid, type, con);
                        break;

                }

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
*/

    private String getLocationTable(InventoryEntity entity) {
        if(entity instanceof  LtrpPlayer) {
            return "player_items";
        } else if(entity instanceof Property) {
            return "property_items";
        } else if(entity instanceof LtrpVehicle) {
            return "vehicle_items";
        }
        return null;
    }

    private String getLocationTable(Class<? extends InventoryEntity> locationClass) {
        if(locationClass.isAssignableFrom(LtrpPlayer.class)) {
            return "player_items";
        } else if(locationClass.isAssignableFrom(Property.class)) {
            return "property_items";
        } else if(locationClass.isAssignableFrom(LtrpVehicle.class)) {
            return "vehicle_items";
        }
        return null;
    }

    private String getLocationColumn(InventoryEntity entity) {
        if(entity instanceof  LtrpPlayer) {
            return "player_id";
        } else if(entity instanceof Property) {
            return "property_id";
        } else if(entity instanceof LtrpVehicle) {
            return "vehicle_id";
        }
        return null;
    }

    private String getLocationColumn(Class<? extends InventoryEntity> locationClass) {
        if(locationClass.isAssignableFrom(LtrpPlayer.class)) {
            return "player_id";
        } else if(locationClass.isAssignableFrom(Property.class)) {
            return "property_id";
        } else if(locationClass.isAssignableFrom(LtrpVehicle.class)) {
            return "vehicle_id";
        }
        return null;
    }

    @Override
    public int insert(Item item) {
        String sql = "INSERT INTO items (type, name, type_class, stackable, amount) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, item.getType().id);
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getClass().getName());
            stmt.setBoolean(4, item.isStackable());
            stmt.setInt(5, item.getAmount());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()) {
                int id = keys.getInt(1);
                item.setUUID(id);
                if(item instanceof ClothingItem)
                    insert((ClothingItem) item, con);
                else if(item instanceof DrinkItem)
                    insert((DrinkItem) item, con);
                else if(item instanceof ConsumableItem)
                    insert((ConsumableItem) item, con);
                else if(item instanceof ContainerItem)
                    insert((ContainerItem) item, con);
                else if(item instanceof DurableItem)
                    insert((DurableItem) item, con);
                else if(item instanceof ItemPhone)
                    insert((ItemPhone) item, con);
                else if(item instanceof RadioItem)
                    insert((RadioItem) item, con);
                else if(item instanceof WeaponItem)
                    insert((WeaponItem) item, con);
                return id;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int insert(Item item, InventoryEntity entity) {
        String location_table = getLocationTable(entity);
        String column_name = getLocationColumn(entity);
        String locationSql = "INSERT INTO " + location_table + " (item_id, " + column_name + ") VALUES(?, ?) ";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement locationStmt = con.prepareStatement(locationSql);
        ) {
            int id = insert(item);
            item.setUUID(id);
            locationStmt.setInt(1, id);
            locationStmt.setInt(2, entity.getUUID());
            locationStmt.execute();
            return id;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void insert(ClothingItem item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_clothing (item_id, model, bone, worn) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, item.getModelId());
            stmt.setInt(3, item.getBone().getValue());
            stmt.setBoolean(4, item.isWorn());
            stmt.execute();
        }
    }

    private void insert(ConsumableItem item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_consumable (item_id, doses) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, item.getDosesLeft());
            stmt.execute();
        }
    }

    private void insert(ContainerItem item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_container (item_id, items, size) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, item.getItemCount());
            stmt.setInt(3, item.getSize());
            stmt.execute();
        }
    }

    private void insert(DrinkItem item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_drink (item_id, special_action) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, item.getSpecialAction().getValue());
            stmt.execute();
        }
    }

    private void insert(DurableItem item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_durable (item_id, durability, max_durability) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, item.getDurability());
            stmt.setInt(3, item.getMaxDurability());
            stmt.execute();
        }
    }

    private void insert(ItemPhone item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_phone (item_id, number) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, item.getPhonenumber());
            stmt.execute();
        }
    }

    private void insert(RadioItem item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_radio (item_id, frequency) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setFloat(2, item.getFrequency());
            stmt.execute();
        }
    }

    private void insert(WeaponItem item, Connection connection) throws SQLException {
        String sql = "INSERT INTO items_weapon (item_id, weapon_id, ammo) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, item.getWeaponData().getModel().getId());
            stmt.setInt(3, item.getWeaponData().getAmmo());
            stmt.execute();
        }
    }

    @Override
    public void delete(Item item) {
        logger.debug("delete called UUID:" + item.getUUID());
        // Now, we should actually delete this row from ALL of the tables, all items_  tables, plus location table
        // Instead, we HOPE that foreign keys will take care of it for us
        String sql = "DELETE FROM items WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, item.getUUID());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Item item) {
        logger.debug("update called item id=" + item.getUUID());
        String sql = "UPDATE items SET type = ?, name = ?, type_class = ?, stackable = ?, amount = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, item.getType().id);
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getClass().getName());
            stmt.setBoolean(4, item.isStackable());
            stmt.setInt(5, item.getAmount());
            stmt.setInt(6, item.getUUID());
            stmt.execute();

            if(item instanceof ClothingItem)
                update((ClothingItem) item, con);
            else if(item instanceof DrinkItem)
                update((DrinkItem) item, con);
            else if(item instanceof ConsumableItem)
                update((ConsumableItem) item, con);
            else if(item instanceof ContainerItem)
                update((ContainerItem) item, con);
            else if(item instanceof DurableItem)
                update((DurableItem) item, con);
            else if(item instanceof ItemPhone)
                update((ItemPhone) item, con);
            else if(item instanceof RadioItem)
                update((RadioItem) item, con);
            else if(item instanceof WeaponItem)
                update((WeaponItem) item, con);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(ClothingItem item, Connection connection) throws SQLException {
        String sql = "UPDATE items_clothing SET model = ?, bone = ?, worn = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getModelId());
            stmt.setInt(2, item.getBone().getValue());
            stmt.setBoolean(3, item.isWorn());
            stmt.setInt(4, item.getUUID());
            stmt.execute();
        }
    }

    private void update(ConsumableItem item, Connection connection) throws SQLException {
        String sql = "UPDATE items_consumable SET doses = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getDosesLeft());
            stmt.setInt(2, item.getUUID());
            stmt.execute();
        }
    }

    private void update(ContainerItem item, Connection connection) throws SQLException {
        String sql = "UPDATE items_container SET items = ?, size = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getItemCount());
            stmt.setInt(2, item.getSize());
            stmt.setInt(3, item.getUUID());
            stmt.execute();
        }
    }

    private void update(DrinkItem item, Connection connection) throws SQLException {
        String sql = "UPDATE items_drink SET special_action = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getSpecialAction().getValue());
            stmt.setInt(2, item.getUUID());
            stmt.execute();
        }
    }

    private void update(DurableItem item, Connection connection) throws SQLException {
        String sql = "UPDATE items_durable SET durability = ?, max_durability = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getDurability());
            stmt.setInt(2, item.getMaxDurability());
            stmt.setInt(3, item.getUUID());
            stmt.execute();
        }
    }

    private void update(ItemPhone item, Connection connection) throws SQLException {
        String sql = "UPDATE items_phone SET number = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getPhonenumber());
            stmt.setInt(2, item.getUUID());
            stmt.execute();
        }
    }

    private void update(RadioItem item, Connection connection) throws SQLException {
        String sql = "UPDATE items_radio SET frequency = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setFloat(1, item.getFrequency());
            stmt.setInt(2, item.getUUID());
            stmt.execute();
        }
    }

    private void update(WeaponItem item, Connection connection) throws SQLException {
        String sql = "UPDATE items_weapon SET weapon_id = ?, ammo = ? WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getWeaponData().getModel().getId());
            stmt.setInt(2, item.getWeaponData().getAmmo());
            stmt.setInt(3, item.getUUID());
            stmt.execute();
        }
    }


    @Override
    public void update(Item item, InventoryEntity inventoryEntity) {
        logger.debug("update called item id=" + item.getUUID() + " location=" + inventoryEntity.getClass().getName() + " locationid=" + inventoryEntity.getUUID());
        String deleteStmt = "DELETE FROM player_items WHERE item_id = ?";
        String deleteStmt2 = "DELETE FROM property_items WHERE item_id = ?";
        String deleteStmt3 = "DELETE FROM vehicle_items WHERE item_id = ?";
        String insertSql = "INSERT INTO " + getLocationTable(inventoryEntity) + " (item_id, " + getLocationColumn(inventoryEntity) + " VALUES (?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(insertSql);
                PreparedStatement stmt1 = con.prepareStatement(deleteStmt);
                PreparedStatement stmt2 = con.prepareStatement(deleteStmt2);
                PreparedStatement stmt3 = con.prepareStatement(deleteStmt3);

        ) {
            stmt1.setInt(1, item.getUUID());
            stmt2.setInt(1, item.getUUID());
            stmt3.setInt(1, item.getUUID());
            // Basically, we delete it from all possible locations and then insert it to the new one
            stmt1.execute();
            stmt2.execute();
            stmt3.execute();

            stmt.setInt(1, item.getUUID());
            stmt.setInt(2, inventoryEntity.getUUID());
            stmt.execute();

            // And now to update the items propertis
            update(item);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Item[] getItems(InventoryEntity entity) {
        List<Item> items = new ArrayList<>();
        String tablename = getLocationTable(entity);
        String columnName = getLocationColumn(entity);
        String sql = "SELECT * FROM " + tablename + " " +
                "LEFT JOIN items ON items.id = " + tablename + ".item_id " +
                "LEFT JOIN items_clothing ON items.id = items_clothing.item_id " +
                "LEFT JOIN items_consumable ON items.id = items_consumable.item_id " +
                "LEFT JOIN items_container ON items.id = items_container.item_id " +
                "LEFT JOIN items_drink ON items.id = items_drink.item_id " +
                "LEFT JOIN items_durable ON items.id = items_durable.item_id " +
                "LEFT JOIN items_phone ON items.id = items_phone.item_id " +
                "LEFT JOIN items_weapon ON items.id = items_weapon.item_id " +
                "LEFT JOIN items_radio ON items.id = items_radio.item_id " +
                "WHERE "  + columnName +  " = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, entity.getUUID());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                Item item = getItem(r);
                // TODO if item null, do something
                items.add(item);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        Item[] i = new Item[items.size()];
        items.toArray(i);
        return i;
    }

    @Override
    public WeaponItem[] getWeaponItems(Class<? extends InventoryEntity> aClass) {
        List<WeaponItem> items = new ArrayList<>();
        String tablename = getLocationTable(aClass);
        String sql = "SELECT * FROM " + tablename + " " +
                "LEFT JOIN items ON items.id = " + tablename + ".item_id " +
                "LEFT JOIN items_weapon ON items.id = items_weapon.item_id";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                Item item = getItem(r);
                items.add((WeaponItem)item);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        WeaponItem[] i = new WeaponItem[items.size()];
        items.toArray(i);
        return i;
    }

    @Override
    public WeaponItem[] getWeaponItems(WeaponModel weaponModel, Class<? extends InventoryEntity> aClass) {
        List<WeaponItem> items = new ArrayList<>();
        String tablename = getLocationTable(aClass);
        String sql = "SELECT * FROM " + tablename + " " +
                "LEFT JOIN items ON items.id = " + tablename + ".item_id " +
                "LEFT JOIN items_weapon ON items.id = items_weapon.item_id WHERE items_weapon.weapon_id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, weaponModel.getId());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                Item item = getItem(r);
                items.add((WeaponItem)item);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        WeaponItem[] i = new WeaponItem[items.size()];
        items.toArray(i);
        return i;
    }

    private Item getItem(ResultSet r) throws SQLException {
        // TODO review unused variables here after all item implementation are done
        Item item = null;
        String typeClass = r.getString("type_class");
        int id = r.getInt("items.id");
        String name = r.getString("name");
        ItemType type = ItemType.getById(r.getInt("type"));
        boolean stackable = r.getBoolean("stackable");
        int amount = r.getInt("amount");
        int model = r.getInt("model");
        PlayerAttachBone bone = PlayerAttachBone.get(r.getInt("bone"));
        boolean worn = r.getBoolean("worn");
        int doses = r.getInt("doses");
        int items = r.getInt("items");
        int size = r.getInt("size");
        SpecialAction action = SpecialAction.get(r.getInt("special_action"));
        int durability = r.getInt("durability");
        int maxDurability = r.getInt("max_durability");
        int number = r.getInt("number");
        float frequency = r.getFloat("frequency");
        int weapon_id = r.getInt("weapon_id");
        int ammo = r.getInt("ammo");
        switch(typeClass) {
            case "lt.ltrp.object.impl.AmphetamineItem":
                item = new AmphetamineItem(id, name, eventManager, doses);
                break;
            case "lt.ltrp.object.impl.WeedItem":
                item = new WeedItemImpl(id, name, eventManager, doses);
                break;
            case "lt.ltrp.object.impl.CocaineItem":
                item = new CocaineItem(id, name, eventManager, doses);
                break;
            case "lt.ltrp.object.impl.HeroinItem":
                item = new HeroinItem(id, name, eventManager, doses);
                break;
            case "lt.ltrp.object.impl.EctazyItem":
                item = new EctazyItem(id, name, eventManager, doses);
                break;
            case "lt.ltrp.object.impl.DrinkItem":
                item = new DrinkItemImpl(id, name, eventManager, type, doses, action);
                break;
            case "lt.ltrp.object.impl.MolotovItem":
                item = new MolotovItem(id, name, eventManager);
                break;
            case "lt.ltrp.object.impl.ToolboxItem":
                item = new ToolboxItem(id, name, eventManager);
                break;
            case "lt.ltrp.object.impl.FuelTankItem":
                item = new FuelTankItem(id, name, eventManager, items, size);
                break;
            case "lt.ltrp.item.WeedSeedItem":
                item = new WeedSeedItemImpl(id, name, eventManager);
                break;
            case "lt.ltrp.item.RadioItem":
                item = new RadioItemImpl(id, name, eventManager, frequency);
                break;
            case "lt.ltrp.object.impl.MaskItem":
                item = new MaskItem(id, name, eventManager, model);
                break;
            case "lt.ltrp.object.impl.FishingRodItem":
                item = new FishingRodItem(id, name, eventManager);
                break;
            case "lt.ltrp.object.impl.SuitcaseItem":
                item = new SuitcaseItem(id, name, eventManager, model);
                break;
            case "lt.ltrp.object.impl.MeleeWeaponItem":
                item = new MeleeWeaponItem(id, name, eventManager, model);
                break;
            case "lt.ltrp.item.WeaponItem":
                item = new WeaponItemImpl(id, name, eventManager, new LtrpWeaponData(WeaponModel.get(weapon_id), ammo, false));
                break;
            case "lt.ltrp.object.impl.DiceItem":
                item = new DiceItem(id, name, eventManager);
                break;
            case "lt.ltrp.object.impl.HouseAudioItem":
                item = new HouseAudioItem(id, name, eventManager);
                break;
            case "lt.ltrp.object.impl.CigarettesItem":
                item = new CigarettesItem(id, name, eventManager, durability);
                break;
            case "lt.ltrp.item.DurableItem":
                item = new DurableItemImpl(id, name, eventManager, type, durability, maxDurability, stackable);
                break;
            case "lt.ltrp.item.ItemPhone":
                item = new ItemPhoneImpl(id, name, eventManager, number, phoneDao.getPhonebook(number));
                break;
            case "lt.ltrp.object.impl.Mp3Item":
                item = new Mp3Item(id, name, eventManager);
                break;
            case "lt.ltrp.object.impl.BoomBoxItem":
                item = new BoomBoxItem(id, name, eventManager);
                break;
            case "lt.ltrp.object.impl.BasicItem":
                item = new BasicItem(id, name, eventManager, type, stackable);
                break;
        }
        if(item != null) {
            item.setAmount(amount);
        }
        return item;
    }
}
