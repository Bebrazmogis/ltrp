package lt.ltrp.item;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.data.Color;
import lt.ltrp.data.Phonecall;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
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
        commandManager.registerCommand("pickup", new Class[]{LtrpPlayer.class}, new String[]{"�aid�jas"}, (player, args) -> {
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
                dialog.setCaption("Skambu�iai");
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
                dialog.setButtonCancel("U�daryti");
                dialog.setClickOkHandler((d, item) -> {
                    ItemPhone phone = (ItemPhone)item.getData();
                    answerPhone(player, phone);
                });

            }
            return true;
        });
        commandManager.registerCommand("hangup", new Class[]{LtrpPlayer.class}, new String[]{"�aid�jas"}, (player, args) -> {
            throw new NotImplementedException("Hangup komanda dar nepadaryta");
            //return true;
        });
        commandManager.registerCommand("invweapon", new Class[]{LtrpPlayer.class}, new String[]{"�aid�jas"}, (player, args) -> {
            if(player.getVehicle() != null) {
                player.sendErrorMessage("Negalite �sid�ti ginklo � inventori� kol esate transporto priemon�je");
            } else if(player.getArmedWeaponData().getModel() == WeaponModel.NONE) {
                player.sendErrorMessage("J�s neesate i�sitrauk�s jokio ginklo.");
            } else if(player.getArmedWeaponData().isJob()) {
                player.sendErrorMessage("Negalite � inventori� �sid�ti darbinio ginklo.");
            } else if(player.getInventory().isFull()) {
                player.sendErrorMessage("J�s� inventorius pilnas");
            } else {
                player.sendMessage(Color.NEWS, "Ginklas s�kmingai �d�tas � inventori�.");
                player.playSound(1057);
                player.removeWeapon(player.getArmedWeaponData());
            }
            return true;
        });

        eventManager.registerHandler(AmxLoadEvent.class, e -> {
           e.getAmxInstance().registerFunction("givePlayerItem", params -> {
               return givePlayerItem((Integer)params[0], (Integer)params[1], (Integer)params[2]) ? 1 : 0;
           }, Integer.class, Integer.class, Integer.class);
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
        phone.getPhonecall().getCaller().sendMessage(contactName + " atsiliep�..");
        phone.getPhonecall().getCaller().sendMessage(Color.YELLOW, "Naudokite t kad galb�tum�te telefonu");
        player.sendMessage(Color.YELLOW, "Naudokite t kad galb�tum�te telefonu");
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
}
