package lt.ltrp.item;

import lt.ltrp.data.*;
import lt.ltrp.item.constant.ItemType;
import lt.ltrp.item.dao.ItemDao;
import lt.ltrp.item.object.Item;
import lt.ltrp.item.object.RadioItem;
import lt.ltrp.item.object.WeaponItem;
import lt.ltrp.item.phone.event.PlayerAnswerPhoneEvent;
import lt.ltrp.item.phone.event.PlayerEndCallEvent;
import lt.ltrp.player.data.Animation;
import lt.ltrp.player.data.DroppedWeaponData;
import lt.ltrp.player.data.LtrpWeaponData;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.object.PlayerCountdown;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Optional;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class ItemCommands {

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String parms) {
        System.out.println("beforeCheck :: itemCommands");
        return true;
    }

    private EventManager eventManager;
    private ItemDao itemDao;

    public ItemCommands(EventManager eventManager, ItemDao itemDao) {
        this.eventManager = eventManager;
        this.itemDao = itemDao;
    }


    @Command
    @CommandHelp("Atidaro inventorø")
    public boolean inv(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.getInventory().show(player);
        return true;
    }

    @Command
    @CommandHelp("Sukuria molotov kokteilá - sprogstantá objektà")
    public boolean makemolotov(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Item newspaper = player.getInventory().getItem(ItemType.Newspaper);
        FuelTankItem fueltank = (FuelTankItem)player.getInventory().getItem(ItemType.Fueltank);
        if(newspaper != null && fueltank != null) {
            if(!newspaper.isStackable() || newspaper.getAmount() == 1 || fueltank.getItemCount() == 1) {
                if(player.getCountdown() == null) {
                    player.sendActionMessage(" iðsitraukia kuro bakelá, laikraðtá ir butelá....");
                    player.applyAnimation(new Animation("BOMBER", "BOM_Plant_2Idle", true, 5000));
                    PlayerCountdown playerCountdown = PlayerCountdown.create(player, 2, true, new PlayerCountdown.PlayerCountdownCallback() {
                        @Override
                        public void onStop(LtrpPlayer player, boolean finished) {
                            if (finished) {
                                player.sendActionMessage("baigia gaminti molotov");
                                fueltank.setItemCount(fueltank.getItemCount() - 1);
                                player.getInventory().remove(newspaper);

                                MolotovItem item = new MolotovItem(eventManager);
                                itemDao.insert(item, player);
                                player.getInventory().add(item);
                            }
                            player.clearAnimations(1);
                        }

                        @Override
                        public void onTick(LtrpPlayer player, int timeremaining) {
                            switch (timeremaining) {
                                case 1:
                                    player.sendActionMessage("paima kuro bakelá, ápyla kuro á butelá...");
                                    break;
                                case 2:
                                    player.sendActionMessage("susuka laikraðtá á rulonà");
                                    break;
                            }
                        }
                    });
                    player.setCountdown(playerCountdown);
                } else
                    player.sendErrorMessage("Jûs jau kaþkà darote.");
            } else
                player.sendErrorMessage("Jûs neturite vietos inventoriuje.");
        } else
            player.sendErrorMessage("Jûs neturite kuro arba laikraðèio.");
        return true;
    }

    @Command
    @CommandHelp("Leidþia atsiliepti jei kas nors skambina")
    public boolean pickup(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
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
            for(ItemPhone ph : phones) {
                if(ph.getPhonecall() != null) {
                    phone = ph;
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
    }

    private void answerPhone(Player p, ItemPhone phone) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(phone.getPhonecall() == null) {
            return;
        }
        eventManager.dispatchEvent(new PlayerAnswerPhoneEvent(player, phone));
    }

    @Command
    @CommandHelp("Baigia telefoniná pokalbá")
    public boolean hangup(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        ItemPhone[] phones = (ItemPhone[])player.getInventory().getItems(ItemType.Phone);
        for(ItemPhone phone : phones) {
            if(phone.getPhonecall() != null) {
                eventManager.dispatchEvent(new PlayerEndCallEvent(player, phone.getPhonecall()));
                return true;
            }
        }
        player.sendErrorMessage("Jums niekas neskambina!");
        return true;
    }

    @Command
    @CommandHelp("Ádeda jûsø dabartiná ginklà á iventoriø")
    public boolean invweapon(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
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
            WeaponItem item = new WeaponItemImpl(eventManager, player.getArmedWeaponData());
            player.getInventory().add(item);
            itemDao.insert(item, player);
            player.removeWeapon(player.getArmedWeaponData());
            return true;
        }
        return true;
    }


    @Command
    @CommandHelp("Iðmeta jûsø laikomà ginklà")
    public boolean leaveGun(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpWeaponData weaponData = player.getArmedWeaponData();
        if(weaponData != null) {
            if(!weaponData.isJob()) {
                if(!player.isInComa()) {
                    weaponData.setDropped(player.getLocation());
                    player.sendActionMessage("iðmeta ginklà kuris atrodo kaip " + weaponData.getModel().getName());
                    return true;
                } else
                    player.sendErrorMessage("Jûs esate komos bûsenoje!");
            } else
                player.sendErrorMessage("Negalite iðmesti darbinio ginklo.");
        } else
            player.sendErrorMessage("Jûs nelaikote ginklo!");
        return true;
    }

    @Command
    @CommandHelp("Paima iðmesta ginklà")
    public boolean grabGun(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Optional<DroppedWeaponData> weaponDataOptional = DroppedWeaponData.getDroppedWeapons().stream().filter(wep -> wep.getLocation().distance(player.getLocation()) < 5.0).findFirst();
        if(!weaponDataOptional.isPresent()) {
            player.sendErrorMessage("Prie jûsø nesimëto ginklø.");
        } else {
            DroppedWeaponData weaponData = weaponDataOptional.get();
            if(player.getInventory().isFull()) {
                player.sendErrorMessage("Jûsø inventorius pilnas.");
            } else {
                WeaponItem item = new WeaponItemImpl(eventManager, weaponData);
                itemDao.insert(item, player);
                player.getInventory().add(item);
                weaponData.destroy();
                player.sendMessage(Color.WHITE, " Ginklas sëkmingai ádëtas á inventoriø. ");
                player.playSound(1057);
                return true;
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Nustato racijos daþná")
    public boolean setFrequency(Player p, @CommandParameter(name = "Daþnis")float frequency) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.getInventory().containsType(ItemType.Radio)) {
            player.sendErrorMessage("Jûs neturite racijos!");
        } else if(RadioItem.PRIVATE_FREQUENCIES.contains(frequency)) {
            player.sendErrorMessage("Jûs negalite prisijungti á ði radijo kanalà.");
        } else {
            RadioItem item = (RadioItem)player.getInventory().getItem(ItemType.Radio);
            item.setFrequency(frequency);
            player.getInfoBox().setRadio(item);
            player.sendActionMessage("Iðsitraukia racijà ir pakeièia daþná..");
            return true;
        }
        return true;
    }
    @Command
    public boolean radio(Player p, @CommandParameter(name = "Tekstas")String msg) {
        return r(p, msg);
    }

    @Command
    @CommandHelp("Leidþia kalbëti per racijà")
    public boolean r(Player player, @CommandParameter(name = "Tekstas")String msg) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(msg == null) {
            p.sendMessage(Color.LIGHTRED, "NOO");
        } else if(!p.getInventory().containsType(ItemType.Radio)) {
            p.sendErrorMessage("Jûs neturite racijos!");
        } else {
            RadioItem item = (RadioItem)p.getInventory().getItem(ItemType.Radio);
            item.sendMessage(p, msg);
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia tyliai kalbëti á racijà")
    public boolean rLow(Player p, @CommandParameter(name = "Tekstas")String msg) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(msg == null || msg.isEmpty()) {
            return false;
        } else if(!player.getInventory().containsType(ItemType.Radio)) {
            player.sendErrorMessage("Jûs neturite racijos!");
        } else {
            RadioItem item = (RadioItem)player.getInventory().getItem(ItemType.Radio);
            item.sendMessage(player, msg, 5f);
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Ridena kauliukus")
    public boolean dice(Player pl) {
        LtrpPlayer p = LtrpPlayer.get(pl);
        if(!p.getInventory().containsType(ItemType.Dice)) {
            p.sendErrorMessage("Jûs neturite þaidimo kauliukø!");
        } else {
            return ((DiceItem)p.getInventory().getItem(ItemType.Dice)).dice(p);
        }
        return true;
    }
}
