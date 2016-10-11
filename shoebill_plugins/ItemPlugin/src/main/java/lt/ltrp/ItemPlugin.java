package lt.ltrp;

import lt.ltrp.constant.ItemType;
import lt.ltrp.dao.DrugAddictionDao;
import lt.ltrp.dao.ItemDao;
import lt.ltrp.dao.PhoneDao;
import lt.ltrp.dao.impl.MySqlDrugAddictionDaoImpl;
import lt.ltrp.dao.impl.SqlItemDao;
import lt.ltrp.dao.impl.SqlPhoneDaoImpl;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.event.GarageLoadedEvent;
import lt.ltrp.event.item.ItemCreateEvent;
import lt.ltrp.event.item.ItemDestroyEvent;
import lt.ltrp.event.item.ItemLocationChangeEvent;
import lt.ltrp.event.item.PlayerDropItemEvent;
import lt.ltrp.event.player.PlayerDrawWeaponItemEvent;
import lt.ltrp.event.property.garage.GarageCreateEvent;
import lt.ltrp.object.*;
import lt.ltrp.object.drug.DrugItem;
import lt.ltrp.object.impl.*;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class ItemPlugin extends Plugin implements ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    

    private EventManagerNode eventManager;
    private ItemDao itemDao;
    private DrugController drugController;
    private PhoneDao phoneDao;
    private PhoneController phoneController;


    @Override
    protected void onEnable() throws Throwable {
        System.out.println("ItemControllerImpl :: onEnable");
        Instance.instance = this;
        this.eventManager = getEventManager().createChildNode();
        if(Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class) != null) {
            load();
        }
        eventManager.registerHandler(ResourceEnableEvent.class, event -> {
            if(event.getResource().getClass().equals(DatabasePlugin.class)) {
                load();
            }
        });
    }

    private void load() {
        DataSource ds = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource();
        this.phoneDao = new SqlPhoneDaoImpl(ds);
        this.itemDao = new SqlItemDao(ds, eventManager, phoneDao);
        DrugAddictionDao drugAddictionDao = new MySqlDrugAddictionDaoImpl(ds);

        drugController = new DrugController(eventManager, drugAddictionDao);
        phoneController = new PhoneController(eventManager, phoneDao);

        PlayerCommandManager commandManager = new PlayerCommandManager(eventManager);

        commandManager.registerCommand("hangup", new Class[0], (player, args) -> {
            throw new NotImplementedException();
            //return true;
        }, null, null, null, null);
        commandManager.registerCommands(new ItemCommands(eventManager, itemDao));
        commandManager.installCommandHandler(HandlerPriority.NORMAL);

        eventManager.registerHandler(AmxLoadEvent.class, e -> {
            registerPawnFunctions(e.getAmxInstance());
        });


        eventManager.registerHandler(PlayerDropItemEvent.class, e -> {
            itemDao.delete(e.getItem());
        });

        eventManager.registerHandler(PlayerDrawWeaponItemEvent.class, e -> {
            itemDao.delete(e.getItem());
        });

        eventManager.registerHandler(ItemDestroyEvent.class, e -> {
            itemDao.delete(e.getItem());
        });

        eventManager.registerHandler(ItemLocationChangeEvent.class, e -> {
            InventoryEntity newOwner = e.getNewInventory().getEntity();
            itemDao.update(e.getItem(), newOwner);
        });

        eventManager.registerHandler(ItemCreateEvent.class, e -> {
            if(e.getOwner() != null)
                itemDao.insert(e.getItem(), e.getOwner());
            else
                itemDao.insert(e.getItem());
        });

        eventManager.registerHandler(GarageLoadedEvent.class, e -> {
            Garage g = e.getGarage();
            Item[] items = itemDao.getItems(g);
            if(g.getInventory() != null)
                g.getInventory().add(items);
            else {
                Inventory inv = Inventory.create(eventManager, g, "Garaþas " + g.getName(), 15);
                inv.add(items);
                g.setInventory(inv);
            }
        });

        logger.debug("Controller:" + ItemController.get() + " dao:" + itemDao);
    }

    @Override
    protected void onDisable() throws Throwable {
        drugController.destroy();
        phoneController.destroy();
    }

    private void registerPawnFunctions(AmxInstance amx) {
        logger.debug("registerPawnFunctions called " + amx.toString());
        amx.registerFunction("tryGivePlayerItemType", params -> {
            logger.debug("tryGivePlayerItemType called");
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            ItemType type = ItemType.getById((Integer)params[1]);
            logger.debug("tryGivePlayerItemType player uid=" + player.getUUID() + " type="+ type.name());
            Item item = null;
            switch(type) {
                case Clothing:
                    item = new ClothingItemImpl("Drabuþis", eventManager, type, (Integer)params[4], PlayerAttachBone.get((Integer)params[5]));
                    break;
                case MeleeWeapon:
                    item = new MeleeWeaponItem(0, "Árankis-Ginklas", eventManager, (Integer)params[4]);
                    break;
                case Suitcase:
                    item = new SuitcaseItem(0, "Lagaminas", eventManager, (Integer)params[4]);
                    break;
                case Mask:
                    item = new MaskItem("Kaukë", eventManager,(Integer)params[4]);
                    break;
                case Phone:
                    int number = ItemController.get().getPhoneDao().generateNumber();
                    item = new ItemPhoneImpl(eventManager, number);
                    break;
                case Cigarettes:
                    item = new CigarettesItem(eventManager);
                    break;
                case Fueltank:
                    item = new FuelTankItem(eventManager);
                    break;
                case FishingRod:
                    item = new FishingRodItem(eventManager);
                    break;
                case FishingBait:
                    item = new DurableItemImpl(0, "Masalas", eventManager, type, (Integer)params[4], (Integer)params[5], (Integer)params[2] == 1);
                    break;
                case FishingBag:
                    item = new ContainerItemImpl(0, "Krepðys þuvims", eventManager, type, (Integer)params[6] == 1, (Integer)params[4], (Integer)params[5]);
                    break;
                case Drink:
                    item = new DrinkItemImpl(0, "Gërimas", eventManager, type, (Integer)params[4], SpecialAction.get((Integer)params[5]));
                    break;
                case Weapon:
                    item = new WeaponItemImpl(eventManager, new LtrpWeaponData(WeaponModel.get((Integer)params[2]), (Integer)params[3], false));
                    break;
                case Amphetamine:
                    item = new AmphetamineItem(eventManager, 1);
                    break;
                case Cocaine:
                    item = new CocaineItem(eventManager, 1);
                    break;
                case Extazy:
                    item = new EctazyItem(eventManager, 1);
                    break;
                case Heroin:
                    item = new HeroinItem(eventManager, 1);
                    break;
                case Toolbox:
                    item = new ToolboxItem(eventManager);
                    break;
                case Mp3Player:
                    item = new Mp3Item(eventManager);
                    break;
                case BoomBox:
                    item = new BoomBoxItem(eventManager);
                    break;
                case Dice:
                    item = new DiceItem(eventManager);
                    break;
                case Radio:
                    item = new RadioItemImpl(eventManager);
                    break;
                case CarAudio:
                    item = new BasicItem(0, "Automagnetola", eventManager, ItemType.CarAudio, false);
                    break;
                default:
                    item = new BasicItem(0, "Daiktas", eventManager, type, (Integer)params[4] == 1);
                    break;
            }
            if(type != ItemType.Weapon && type != ItemType.Phone && type != ItemType.Cigarettes)
                item.setAmount((Integer)params[3]);
            boolean success = player.getInventory().tryAdd(item);
            if(success)
                itemDao.insert(item, player);

            return success ? 1 : 0;
        }, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);

        amx.registerFunction("removePlayerItem", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            if(player != null) {
                ItemType type = ItemType.getById((Integer)params[1]);
                Item[] items = player.getInventory().getItems(type);
                int count = (Integer)params[3];
                for(int i = count > items.length ? items.length : count; i >= 0 && i < items.length; i--) {
                    player.getInventory().remove(items[i]);
                }
                return 1;
            }
            return 0;
        }, Integer.class, Integer.class, Integer.class, Integer.class);

        amx.registerFunction("isPlayerInventoryFull", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            boolean full = false;
            if(player != null) {
                full = player.getInventory().isFull();
            }
            return full ? 1 : 0;
        }, Integer.class);

        amx.registerFunction("removePlayerDrugItems", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            if(player != null) {
                for(Item item : player.getInventory().getItems(DrugItem.class)) {
                    player.getInventory().remove(item);
                }
            }
            return player == null ? 1 : 0;
        }, Integer.class);

    }

    @Override
    public WeedSeedItem createWeedSeed(EventManager eventManager) {
        WeedSeedItemImpl item = new WeedSeedItemImpl(eventManager);
        eventManager.dispatchEvent(new ItemCreateEvent(item, null));
        return item;
    }

    @Override
    public WeedItem createWeed(EventManager eventManager, int doses) {
        WeedItemImpl item = new WeedItemImpl(eventManager, doses);
        eventManager.dispatchEvent(new ItemCreateEvent(item, null));
        return item;
    }

    @Override
    public Inventory createInventory(EventManager eventManager, InventoryEntity inventoryEntity, String s, int size) {
        return new FixedSizeInventory(eventManager, s, size, inventoryEntity);
    }

    @Override
    public Item createItem(ItemType itemType, InventoryEntity entity, EventManager eventManager) {
        Item item = null;
        switch(itemType) {
            case Radio:
                item = new RadioItemImpl(eventManager);
                break;
            case Dice:
                item = new DiceItem(eventManager);
                break;
            case FishingRod:
                item = new FishingRodItem(eventManager);
                break;
            case Fueltank:
                item = new FuelTankItem(eventManager);
                break;
            case Cigarettes:
                item = new CigarettesItem(eventManager);
                break;
            case Phone:
                item = new ItemPhoneImpl(eventManager, getPhoneDao().generateNumber());
                break;
            case WeedSeed:
                item = new WeedSeedItemImpl(eventManager);
                break;
            case Molotov:
                item = new MolotovItem(eventManager);
                break;
            case HouseAudio:
                item = new HouseAudioItem(eventManager);
                break;
            case Weed:
                item = new WeedItemImpl(eventManager, 1);
                break;
            case Toolbox:
                item = new ToolboxItem(eventManager);
                break;
            case Mp3Player:
                item = new Mp3Item(eventManager);
                break;
            case BoomBox:
                item = new BoomBoxItem(eventManager);
                break;
        }
        if(item != null)
            item.setAmount(1);
        eventManager.dispatchEvent(new ItemCreateEvent(item, entity));
        return item;
    }

    @Override
    public Item createItem(ItemType itemType, String s, InventoryEntity inventoryEntity, EventManager eventManager) {
        Item item = createItem(itemType, inventoryEntity, eventManager);
        item.setName(s);
        return item;
    }

    @Override
    public Item createItem(ItemType itemType, String s, SpecialAction specialAction, InventoryEntity inventoryEntity, EventManager eventManager) {
        Item item = null;
        switch(itemType) {
            case Drink:
                item = new DrinkItemImpl(0, s, eventManager, itemType, 1, specialAction);
                item.setAmount(1);
                break;
            default:
                item = createItem(itemType, s, inventoryEntity, eventManager);
        }
        return item;
    }

    public WeaponItem createWeaponItem(WeaponModel weaponModel, int i, InventoryEntity inventoryEntity, EventManager eventManager) {
        WeaponItemImpl item = new WeaponItemImpl(eventManager, new LtrpWeaponData(weaponModel, i, false));
        eventManager.dispatchEvent(new ItemCreateEvent(item, inventoryEntity));
        return item;
    }

    /*


        FishingBait(10),
        FishingBag(11),
        Lighter(12),
        Clothing(16),
        MeleeWeapon(17),
        Suitcase(18),
        Mask(19),
        Weapon(20),
        Drink(21),
        Syringe(23),
        Newspaper(25),
        Amphetamine(26),
        Cocaine(27),
        Extazy(28),
        Heroin(29),
        MetaAmphetamine(30),
        Pcp(31),
        DmvTheory(34),
        Prescription(35),
        Materials(36),
        CarAudio(40)
     */
    @Override
    public ItemDao getItemDao() {
        return itemDao;
    }

    @Override
    public PhoneDao getPhoneDao() {
        return phoneDao;
    }

}
