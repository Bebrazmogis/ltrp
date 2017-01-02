package lt.ltrp.command;

import lt.ltrp.WeaponDropPlugin;
import lt.ltrp.data.DroppedWeaponData;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.object.Player;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class WeaponDropCommands extends Commands {


    public boolean leaveGun(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.isInComa())
            player.sendErrorMessage("Jûs esate komos bûsenoje!");
        else if(player.getArmedWeapon() == null || player.getArmedWeapon() == WeaponModel.NONE)
            player.sendErrorMessage("Jûs nelaikote ginklo rankose!");
        else if(player.isInAnyVehicle())
            player.sendErrorMessage("Bûdamas transporto priemonëje iðmesti ginklø negalite.");
        else {
            LtrpWeaponData weaponData = player.getArmedWeaponData();
            // fail-safe
            if(weaponData == null)
                return false;
            if(weaponData.isJob())
                player.sendErrorMessage("Darbiniø ginklø mëtyti negalite");
            else {
                WeaponDropPlugin.get(WeaponDropPlugin.class).dropWeapon(weaponData, player.getLocation());
                player.sendActionMessage("iðmeta ginklà kuris atrodo kaip " + weaponData.getModel().getName());
                player.removeWeapon(weaponData);
            }
        }
        return true;
    }

    public boolean grabGun(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.isInComa())
            player.sendErrorMessage("Jûs esate komos bûsenoje.");
        else {
            Optional<DroppedWeaponData> optionalDropped = DroppedWeaponData.getDroppedWeapons().stream()
                    .filter(w -> !w.isDestroyed() && w.getLocation().distance(player.getLocation()) < 5f)
                    .findFirst();
            if(!optionalDropped.isPresent())
                player.sendErrorMessage("Prie jûsø nesimëto joks ginklas.");
            else {
                DroppedWeaponData wep = optionalDropped.get();
                if(player.ownsWeapon(wep.getModel()))
                    player.sendErrorMessage("Jûs jau turite toká ginklà.");
                else if(player.isWeaponSlotUsed(wep.getModel().getSlot()))
                    player.sendErrorMessage("Jûs jau turite tokio tipo ginklà.");
                else {
                    player.giveWeapon(new LtrpWeaponData(wep.getModel(), wep.getAmmo(), false));
                    wep.destroy();
                    player.setArmedWeapon(wep.getModel());
                }
            }
        }
        return true;
    }

}
