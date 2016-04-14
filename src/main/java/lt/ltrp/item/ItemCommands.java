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
    @CommandHelp("Atidaro inventor�")
    public boolean inv(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        player.getInventory().show(player);
        return true;
    }

    @Command
    @CommandHelp("Sukuria molotov kokteil� - sprogstant� objekt�")
    public boolean makemolotov(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Item newspaper = player.getInventory().getItem(ItemType.Newspaper);
        FuelTankItem fueltank = (FuelTankItem)player.getInventory().getItem(ItemType.Fueltank);
        if(newspaper != null && fueltank != null) {
            if(!newspaper.isStackable() || newspaper.getAmount() == 1 || fueltank.getItemCount() == 1) {
                if(player.getCountdown() == null) {
                    player.sendActionMessage(" i�sitraukia kuro bakel�, laikra�t� ir butel�....");
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
                                    player.sendActionMessage("paima kuro bakel�, �pyla kuro � butel�...");
                                    break;
                                case 2:
                                    player.sendActionMessage("susuka laikra�t� � rulon�");
                                    break;
                            }
                        }
                    });
                    player.setCountdown(playerCountdown);
                } else
                    player.sendErrorMessage("J�s jau ka�k� darote.");
            } else
                player.sendErrorMessage("J�s neturite vietos inventoriuje.");
        } else
            player.sendErrorMessage("J�s neturite kuro arba laikra��io.");
        return true;
    }

    @Command
    @CommandHelp("Leid�ia atsiliepti jei kas nors skambina")
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
    }

    private void answerPhone(Player p, ItemPhone phone) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(phone.getPhonecall() == null) {
            return;
        }
        eventManager.dispatchEvent(new PlayerAnswerPhoneEvent(player, phone));
    }

    @Command
    @CommandHelp("Baigia telefonin� pokalb�")
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
    @CommandHelp("�deda j�s� dabartin� ginkl� � iventori�")
    public boolean invweapon(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
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
            WeaponItem item = new WeaponItemImpl(eventManager, player.getArmedWeaponData());
            player.getInventory().add(item);
            itemDao.insert(item, player);
            player.removeWeapon(player.getArmedWeaponData());
            return true;
        }
        return true;
    }


    @Command
    @CommandHelp("I�meta j�s� laikom� ginkl�")
    public boolean leaveGun(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpWeaponData weaponData = player.getArmedWeaponData();
        if(weaponData != null) {
            if(!weaponData.isJob()) {
                if(!player.isInComa()) {
                    weaponData.setDropped(player.getLocation());
                    player.sendActionMessage("i�meta ginkl� kuris atrodo kaip " + weaponData.getModel().getName());
                    return true;
                } else
                    player.sendErrorMessage("J�s esate komos b�senoje!");
            } else
                player.sendErrorMessage("Negalite i�mesti darbinio ginklo.");
        } else
            player.sendErrorMessage("J�s nelaikote ginklo!");
        return true;
    }

    @Command
    @CommandHelp("Paima i�mesta ginkl�")
    public boolean grabGun(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Optional<DroppedWeaponData> weaponDataOptional = DroppedWeaponData.getDroppedWeapons().stream().filter(wep -> wep.getLocation().distance(player.getLocation()) < 5.0).findFirst();
        if(!weaponDataOptional.isPresent()) {
            player.sendErrorMessage("Prie j�s� nesim�to ginkl�.");
        } else {
            DroppedWeaponData weaponData = weaponDataOptional.get();
            if(player.getInventory().isFull()) {
                player.sendErrorMessage("J�s� inventorius pilnas.");
            } else {
                WeaponItem item = new WeaponItemImpl(eventManager, weaponData);
                itemDao.insert(item, player);
                player.getInventory().add(item);
                weaponData.destroy();
                player.sendMessage(Color.WHITE, " Ginklas s�kmingai �d�tas � inventori�. ");
                player.playSound(1057);
                return true;
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Nustato racijos da�n�")
    public boolean setFrequency(Player p, @CommandParameter(name = "Da�nis")float frequency) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.getInventory().containsType(ItemType.Radio)) {
            player.sendErrorMessage("J�s neturite racijos!");
        } else if(RadioItem.PRIVATE_FREQUENCIES.contains(frequency)) {
            player.sendErrorMessage("J�s negalite prisijungti � �i radijo kanal�.");
        } else {
            RadioItem item = (RadioItem)player.getInventory().getItem(ItemType.Radio);
            item.setFrequency(frequency);
            player.getInfoBox().setRadio(item);
            player.sendActionMessage("I�sitraukia racij� ir pakei�ia da�n�..");
            return true;
        }
        return true;
    }
    @Command
    public boolean radio(Player p, @CommandParameter(name = "Tekstas")String msg) {
        return r(p, msg);
    }

    @Command
    @CommandHelp("Leid�ia kalb�ti per racij�")
    public boolean r(Player player, @CommandParameter(name = "Tekstas")String msg) {
        LtrpPlayer p = LtrpPlayer.get(player);
        if(msg == null) {
            p.sendMessage(Color.LIGHTRED, "NOO");
        } else if(!p.getInventory().containsType(ItemType.Radio)) {
            p.sendErrorMessage("J�s neturite racijos!");
        } else {
            RadioItem item = (RadioItem)p.getInventory().getItem(ItemType.Radio);
            item.sendMessage(p, msg);
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia tyliai kalb�ti � racij�")
    public boolean rLow(Player p, @CommandParameter(name = "Tekstas")String msg) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(msg == null || msg.isEmpty()) {
            return false;
        } else if(!player.getInventory().containsType(ItemType.Radio)) {
            player.sendErrorMessage("J�s neturite racijos!");
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
            p.sendErrorMessage("J�s neturite �aidimo kauliuk�!");
        } else {
            return ((DiceItem)p.getInventory().getItem(ItemType.Dice)).dice(p);
        }
        return true;
    }
}
