package lt.ltrp.item;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.data.Color;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.data.Phonecall;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.NotImplementedException;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class ItemController {

    private static EventManagerNode eventManager;


    protected static EventManager getEventManager() {
        return eventManager;
    }

    private static ItemController instance;

    public static void init() {
        eventManager = LtrpGamemode.get().getEventManager().createChildNode();
        instance = new ItemController();
    }

    protected ItemController() {
        PlayerCommandManager commandManager = new PlayerCommandManager(HandlerPriority.NORMAL, eventManager);
        commandManager.registerCommand("pickup", new Class[]{LtrpPlayer.class}, new String[]{"Þaidëjas"}, (player, args) -> {
            ItemPhone[] phones = player.getInventory().getItems(ItemPhone.class);
            //ItemPhone[] phones = (ItemPhone[])player.getInventory().getItems(ItemType.Phone);
            int calledPhones = 0;
            for(ItemPhone phone : phones)
                if(phone.getPhonecall() != null)
                    calledPhones++;

            // A player can be NOT called at all, called on one phone OR called on multiple phones
            if(calledPhones == 0) {
                player.sendErrorMessage("Jums niekas neskambina");
            } else if(calledPhones == 1) {
                ItemPhone phone = null;
                for(ItemPhone p : phones) {
                    if(p.getPhonecall() != null) {
                        phone = p;
                        break;
                    }
                }
                answerPhone(player, phone);
            } else {
                ListDialog dialog = ListDialog.create(player, eventManager).build();
                dialog.setCaption("Skambuèiai");
                for(ItemPhone phone : phones) {
                    if(phone.getPhonecall() == null) {
                        continue;
                    }
                    ListDialogItem item = new ListDialogItem();
                    item.setItemText(Integer.toString(phone.getPhonecall().getCallerPhone().getPhonenumber()));
                    item.setData(phone);
                    dialog.addItem(item);
                }
                dialog.setButtonOk("Atsiliepti");
                dialog.setButtonCancel("Uþdaryti");
                dialog.setClickOkHandler((d, item) -> {
                    ItemPhone phone = (ItemPhone)item.getData();
                    answerPhone(player, phone);
                });

            }
            return true;
        });
        commandManager.registerCommand("hangup", new Class[]{LtrpPlayer.class}, new String[]{"þaidëjas"}, (player, args) -> {
            throw new NotImplementedException("Hangup komanda dar nepadaryta");
            //return true;
        });
        commandManager.registerCommand("invweapon", new Class[]{LtrpPlayer.class}, new String[]{"Þaidëjas"}, (player, args) -> {
            if(player.getVehicle() != null) {
                player.sendErrorMessage("Negalite ásidëti ginklo á inventoriø kol esate transporto priemonëje");
            } else if(player.getArmedWeaponData().getModel() == WeaponModel.NONE) {
                player.sendErrorMessage("Jûs neesate iðsitraukæs jokio ginklo.");
            } else if(player.getArmedWeaponData().isJob()) {
                player.sendErrorMessage("Negalite á inventoriø ásidëti darbinio ginklo.");
            } else if(player.getInventory().isFull()) {
                player.sendErrorMessage("Jûsø inventorius pilnas");
            } else {
                player.sendMessage(Color.NEWS, "Ginklas sëkmingai ádëtas á inventoriø.");
                player.playSound(1057);
                player.removeWeapon(player.getArmedWeaponData());
            }
            return false;
        });

        commandManager.registerCommands(new ItemCommands());

        eventManager.registerHandler(AmxLoadEvent.class, e -> {
           registerPawnFunctions(e.getAmxInstance());
        });


    }

    private void answerPhone(LtrpPlayer player, ItemPhone phone) {
        if(phone.getPhonecall() == null) {
            return;
        }
        phone.getPhonecall().setState(Phonecall.PhonecallState.Talking);
        String contactName = phone.getPhonecall().getCallerPhone().getPhonebook().getContactName(phone.getPhonenumber());
        if(contactName == null) {
            contactName = ""+phone.getPhonenumber();
        }
        phone.getPhonecall().getCaller().sendMessage(contactName + " atsiliepë..");
        phone.getPhonecall().getCaller().sendMessage(Color.YELLOW, "Naudokite t kad galbëtumëte telefonu");
        player.sendMessage(Color.YELLOW, "Naudokite t kad galbëtumëte telefonu");
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
        amx.registerFunction("givePlayerItem", params -> {
            return givePlayerItem((Integer) params[0], (Integer) params[1], (Integer) params[2]) ? 1 : 0;
        }, Integer.class, Integer.class, Integer.class);

        amx.registerFunction("tryGivePlayerItemType", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            ItemType type = ItemType.getById((Integer)params[1]);
            Item item = null;
            switch(type) {
                case Clothing:
                    item = new ClothingItem("Drabuþis", type, (Integer)params[4], PlayerAttachBone.get((Integer)params[5]));
                    break;
                case MeleeWeapon:
                    item = new MeleeWeaponItem("Árankis-Ginklas", type, (Integer)params[4], PlayerAttachBone.get((Integer)params[5]), null, 0.0f);
                    break;
                case Suitcase:
                    item = new SuitcaseItem("Lagaminas", (Integer)params[4]);
                    break;
                case Mask:
                    item = new MaskItem("Kaukë",(Integer)params[4]);
                    break;
                case Phone:
                    int number = LtrpGamemode.getDao().getItemDao().generatePhonenumber();
                    item = new ItemPhone(String.format("Telefonas(%d)", number), number);
                    break;
                case Cigarettes:
                    item = new CigarettesItem("Cigaretës", 20);
                    break;
                case Fueltank:
                    item = new FuelTankItem();
                    break;
                case FishingRod:
                    item = new FishingRodItem();
                    break;
                case FishingBait:
                    item = new DurableItem("Masalas", type, (Integer)params[4], (Integer)params[5], (Integer)params[6] == 1);
                    break;
                case FishingBag:
                    item = new ContainerItem("Krepðys þuvims", type, (Integer)params[6] == 1, (Integer)params[4], (Integer)params[5]);
                    break;
                case Drink:
                    item = new DrinkItem("Gëirimas", type, (Integer)params[4], SpecialAction.get((Integer)params[5]));
                    break;
                case Weapon:
                    item = new WeaponItem(new LtrpWeaponData(WeaponModel.get((Integer)params[2]), (Integer)params[3], false));
                    break;
                case Amphetamine:
                    item = new AmphetamineItem("Amfetaminas", 1);
                    break;
                case Cocaine:
                    item = new CocaineItem("Kokainas", 1);
                    break;
                case Extazy:
                    item = new EctazyItem("Ekstazi", 1);
                    break;
                case Heroin:
                    item = new HeroinItem("Heroinas", 1);
                    break;
                case MetaAmphetamine:
                    item = new MetaamphetamineItem("Metamfetaminas", 1);
                    break;
                case Pcp:
                    item = new PcpItem("PCP", 1);
                    break;
                default:
                    item = new BasicItem("Daiktas", type, (Integer)params[5] == 1);
                    break;
            }
            item.setAmount((Integer)params[3]);
            boolean success = player.getInventory().tryAdd(item);
            if(success)
                LtrpGamemode.getDao().getItemDao().insert(item, LtrpPlayer.class, player.getUserId());

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

    }

}
