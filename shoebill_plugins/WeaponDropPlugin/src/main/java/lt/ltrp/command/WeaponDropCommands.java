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
            player.sendErrorMessage("J�s esate komos b�senoje!");
        else if(player.getArmedWeapon() == null || player.getArmedWeapon() == WeaponModel.NONE)
            player.sendErrorMessage("J�s nelaikote ginklo rankose!");
        else if(player.isInAnyVehicle())
            player.sendErrorMessage("B�damas transporto priemon�je i�mesti ginkl� negalite.");
        else {
            LtrpWeaponData weaponData = player.getArmedWeaponData();
            // fail-safe
            if(weaponData == null)
                return false;
            if(weaponData.isJob())
                player.sendErrorMessage("Darbini� ginkl� m�tyti negalite");
            else {
                WeaponDropPlugin.get(WeaponDropPlugin.class).dropWeapon(weaponData, player.getLocation());
                player.sendActionMessage("i�meta ginkl� kuris atrodo kaip " + weaponData.getModel().getName());
                player.removeWeapon(weaponData);
            }
        }
        return true;
    }

    public boolean grabGun(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.isInComa())
            player.sendErrorMessage("J�s esate komos b�senoje.");
        else {
            Optional<DroppedWeaponData> optionalDropped = DroppedWeaponData.getDroppedWeapons().stream()
                    .filter(w -> !w.isDestroyed() && w.getLocation().distance(player.getLocation()) < 5f)
                    .findFirst();
            if(!optionalDropped.isPresent())
                player.sendErrorMessage("Prie j�s� nesim�to joks ginklas.");
            else {
                DroppedWeaponData wep = optionalDropped.get();
                if(player.ownsWeapon(wep.getModel()))
                    player.sendErrorMessage("J�s jau turite tok� ginkl�.");
                else if(player.isWeaponSlotUsed(wep.getModel().getSlot()))
                    player.sendErrorMessage("J�s jau turite tokio tipo ginkl�.");
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
