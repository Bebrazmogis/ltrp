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
    @CommandHelp("Atidaro inventorø")
    public boolean inv(LtrpPlayer player) {
        player.getInventory().show(player);
        return true;
    }

    @Command
    @CommandHelp("Sukuria molotov kokteilá - sprogstantá objektà")
    public boolean makemolotov(LtrpPlayer player) {
        Item newspaper = player.getInventory().getItem(ItemType.Newspaper);
        FuelTankItem fueltank = (FuelTankItem)player.getInventory().getItem(ItemType.Fueltank);
        if(newspaper != null && fueltank != null) {
            if(!newspaper.isStackable() || newspaper.getAmount() == 1 || fueltank.getItemCount() == 1) {
                if(player.getCountdown() == null) {
                    player.sendActionMessage(" iðsitraukia kuro bakelá, laikraðtá ir butelá....");
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
        return false;
    }

    @Command
    @CommandHelp("Leidþia atsiliepti jei kas nors skambina")
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


    @Command
    @CommandHelp("Ádeda jûsø dabartiná ginklà á iventoriø")
    public boolean invweapon(LtrpPlayer player) {
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
    }


    @Command
    @CommandHelp("Iðmeta jûsø laikomà ginklà")
    public boolean leaveGun(LtrpPlayer player) {
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
        return false;
    }

    @Command
    @CommandHelp("Paima iðmesta ginklà")
    public boolean grabGun(LtrpPlayer player) {
        Optional<DroppedWeaponData> weaponDataOptional = DroppedWeaponData.getDroppedWeapons().stream().filter(wep -> wep.getLocation().distance(player.getLocation()) < 5.0).findFirst();
        if(!weaponDataOptional.isPresent()) {
            player.sendErrorMessage("Prie jûsø nesimëto ginklø.");
        } else {
            DroppedWeaponData weaponData = weaponDataOptional.get();
            if(player.getInventory().isFull()) {
                player.sendErrorMessage("Jûsø inventorius pilnas.");
            } else {
                WeaponItem item = new WeaponItem(weaponData);
                LtrpGamemode.getDao().getItemDao().insert(item, LtrpPlayer.class, player.getUserId());
                player.getInventory().add(item);
                weaponData.destroy();
                player.sendMessage(Color.WHITE, " Ginklas sëkmingai ádëtas á inventoriø. ");
                player.playSound(1057);
                return true;
            }
        }
        return false;
    }

    @Command
    @CommandHelp("Nustato racijos daþná")
    public boolean setFrequency(LtrpPlayer player, float frequency) {
        if(!player.getInventory().containsType(ItemType.Radio)) {
            player.sendErrorMessage("Jûs neturite racijos!");
        } else {
            RadioItem item = (RadioItem)player.getInventory().getItem(ItemType.Radio);
            item.setFrequency(frequency);
            player.getInfoBox().setRadio(item);
            player.sendActionMessage("Iðsitraukia racijà ir pakeièia daþná..");
            return true;
        }
        return false;
    }
}
