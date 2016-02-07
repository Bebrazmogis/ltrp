package lt.ltrp.item;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.data.*;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerCountdown;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.object.Timer;

import java.util.Optional;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class ItemCommands {


    @Command
    @CommandHelp("Atidaro inventor�")
    public boolean inv(LtrpPlayer player) {
        player.getInventory().show(player);
        return true;
    }

    @Command
    @CommandHelp("Sukuria molotov kokteil� - sprogstant� objekt�")
    public boolean makemolotov(LtrpPlayer player) {
        Item newspaper = player.getInventory().getItem(ItemType.Newspaper);
        FuelTankItem fueltank = (FuelTankItem)player.getInventory().getItem(ItemType.Fueltank);
        if(newspaper != null && fueltank != null) {
            if(!newspaper.isStackable() || newspaper.getAmount() == 1 || fueltank.getItemCount() == 1) {
                if(player.getCountdown() == null) {
                    player.sendActionMessage(" i�sitraukia kuro bakel�, laikra�t� ir butel�....");
                    player.applyAnimation(new Animation("BOMBER", "BOM_Plant_2Idle", true, 5000));
                    PlayerCountdown playerCountdown = new PlayerCountdown(player, 2, true, new PlayerCountdown.PlayerCountdownCallback() {
                        @Override
                        public void onStop(LtrpPlayer player) {
                            player.sendActionMessage("baigia gaminti molotov");
                            fueltank.setItemCount(fueltank.getItemCount()-1);
                            player.getInventory().remove(newspaper);

                            MolotovItem item = new MolotovItem();
                            LtrpGamemode.getDao().getItemDao().insert(item, LtrpPlayer.class, player.getUserId());
                            player.getInventory().add(item);
                            player.clearAnimations(1);
                        }
                        @Override
                        public void onTick(LtrpPlayer player, int timeremaining) {
                            switch(timeremaining) {
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
        return false;
    }

    @Command
    @CommandHelp("Leid�ia atsiliepti jei kas nors skambina")
    public boolean pickup(LtrpPlayer player) {
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
            ListDialog dialog = ListDialog.create(player, ItemController.getEventManager()).build();
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


    @Command
    @CommandHelp("�deda j�s� dabartin� ginkl� � iventori�")
    public boolean invweapon(LtrpPlayer player) {
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
        return false;
    }


    @Command
    @CommandHelp("I�meta j�s� laikom� ginkl�")
    public boolean leaveGun(LtrpPlayer player) {
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
        return false;
    }

    @Command
    @CommandHelp("Paima i�mesta ginkl�")
    public boolean grabGun(LtrpPlayer player) {
        Optional<DroppedWeaponData> weaponDataOptional = DroppedWeaponData.getDroppedWeapons().stream().filter(wep -> wep.getLocation().distance(player.getLocation()) < 5.0).findFirst();
        if(!weaponDataOptional.isPresent()) {
            player.sendErrorMessage("Prie j�s� nesim�to ginkl�.");
        } else {
            DroppedWeaponData weaponData = weaponDataOptional.get();
            if(player.getInventory().isFull()) {
                player.sendErrorMessage("J�s� inventorius pilnas.");
            } else {
                WeaponItem item = new WeaponItem(weaponData);
                LtrpGamemode.getDao().getItemDao().insert(item, LtrpPlayer.class, player.getUserId());
                player.getInventory().add(item);
                weaponData.destroy();
                player.sendMessage(Color.WHITE, " Ginklas s�kmingai �d�tas � inventori�. ");
                player.playSound(1057);
                return true;
            }
        }
        return false;
    }

    @Command
    @CommandHelp("Nustato racijos da�n�")
    public boolean setFrequency(LtrpPlayer player, float frequency) {
        if(!player.getInventory().containsType(ItemType.Radio)) {
            player.sendErrorMessage("J�s neturite racijos!");
        } else {
            RadioItem item = (RadioItem)player.getInventory().getItem(ItemType.Radio);
            item.setFrequency(frequency);
            player.getInfoBox().setRadio(item);
            player.sendActionMessage("I�sitraukia racij� ir pakei�ia da�n�..");
            return true;
        }
        return false;
    }
}
