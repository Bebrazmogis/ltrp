package lt.ltrp.item;

import lt.ltrp.LtrpGamemodeImpl;
import lt.ltrp.item.constant.ItemType;
import lt.ltrp.item.dao.ItemDao;
import lt.ltrp.item.drug.DrugController;
import lt.ltrp.item.event.*;
import lt.ltrp.item.object.Inventory;
import lt.ltrp.item.object.InventoryEntity;
import lt.ltrp.item.object.Item;
import lt.ltrp.item.object.drug.DrugItem;
import lt.ltrp.item.phone.PhoneController;
import lt.ltrp.player.data.LtrpWeaponData;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    

    private EventManagerNode eventManager;
    private ItemDao itemDao;
    private DrugController drugController;
    private PhoneController phoneController;

    public ItemController(EventManager eventManager, ItemDao itemDao) {
        this.itemDao = itemDao;
        this.eventManager = eventManager.createChildNode();


        drugController = new DrugController(eventManager, LtrpGamemodeImpl.getDao().getDrugAddictionDao());
        phoneController = new PhoneController(eventManager, LtrpGamemodeImpl.getDao().getPhoneDao());

        PlayerCommandManager commandManager = new PlayerCommandManager(eventManager);
        System.out.println("ItemController registering commands");
        commandManager.registerCommand("hangup", new Class[0], (player, args) -> {
            throw new NotImplementedException("Hangup komanda dar nepadaryta");
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
            itemDao.insert(e.getItem(), e.getOwner());
        });

    }

    public void destroy() {
        drugController.destroy();
        phoneController.destroy();
    }


    // Legacy code. Conversion between Pawn item id and Item instance
    public static Item getItemById(int id) {
        Item item = null;
        // this might be the issue, wouldnt you say so>?ye LOL xD
        // it should be a giant switch .... so too lazy to wirte it easy

        ItemType type = ItemType.getById(id);
        switch(type) {

        }

        return item;
    }


    public boolean givePlayerItem(int playerid, int itemid, int amount) {
        LtrpPlayer p = LtrpPlayer.get(playerid);
        Item item = getItemById(itemid);
        if(item != null && p != null) {
            Inventory inv = p.getInventory();
           // if (inv.contains(item)) {

            //} else {
                if(!inv.isFull()) {
                    inv.add(item);
                    return true;
                } else
                    return false;
            //}
        } else {
            return false;
        }
    }

    private void registerPawnFunctions(AmxInstance amx) {
        logger.debug("registerPawnFunctions called " + amx.toString());
        amx.registerFunction("givePlayerItem", params -> {
            return givePlayerItem((Integer) params[0], (Integer) params[1], (Integer) params[2]) ? 1 : 0;
        }, Integer.class, Integer.class, Integer.class);

        amx.registerFunction("tryGivePlayerItemType", params -> {
            logger.debug("tryGivePlayerItemType called");
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            ItemType type = ItemType.getById((Integer)params[1]);
            logger.debug("tryGivePlayerItemType player uid=" + player.getUUID() + " type="+ type.name());
            Item item = null;
            switch(type) {
                case Clothing:
                    item = new ClothingItem("Drabuþis", eventManager, type, (Integer)params[4], PlayerAttachBone.get((Integer)params[5]));
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
                    int number = LtrpGamemodeImpl.getDao().getPhoneDao().generateNumber();
                    item = new ItemPhone(eventManager, number);
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
                    item = new ContainerItem(0, "Krepðys þuvims", eventManager, type, (Integer)params[6] == 1, (Integer)params[4], (Integer)params[5]);
                    break;
                case Drink:
                    item = new DrinkItem(0, "Gërimas", eventManager, type, (Integer)params[4], SpecialAction.get((Integer)params[5]));
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

}
