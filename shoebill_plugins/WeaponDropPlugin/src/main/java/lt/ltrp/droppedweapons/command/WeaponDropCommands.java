package lt.ltrp.droppedweapons.command;

import lt.ltrp.command.Commands;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.droppedweapons.WeaponDropPlugin;
import lt.ltrp.droppedweapons.object.DroppedWeaponData;
import lt.ltrp.player.PlayerPlugin;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.resource.ResourceManager;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class WeaponDropCommands extends Commands {

    private PlayerPlugin playerPlugin;
    private WeaponDropPlugin dropPlugin;

    public WeaponDropCommands(WeaponDropPlugin dropPlugin) {
        this.dropPlugin = dropPlugin;
        playerPlugin = ResourceManager.get().getPlugin(PlayerPlugin.class);
    }

    public boolean leaveGun(Player p) {
        LtrpPlayer player = playerPlugin.get(p);
        if(player == null)
            return false;

        if(player.isInComa())
            player.sendErrorMessage("J�s esate komos b�senoje!");
        else if(player.getArmedWeaponData() == null || player.getArmedWeaponData().getWeaponData().model == WeaponModel.NONE)
            player.sendErrorMessage("J�s nelaikote ginklo rankose!");
        else if(player.getPlayer().isInAnyVehicle())
            player.sendErrorMessage("B�damas transporto priemon�je i�mesti ginkl� negalite.");
        else {
            LtrpWeaponData weaponData = player.getArmedWeaponData();
            // fail-safe
            if(weaponData == null)
                return false;
            if(weaponData.isJob())
                player.sendErrorMessage("Darbini� ginkl� m�tyti negalite");
            else {
                dropPlugin.dropWeapon(weaponData, player.getPlayer().getLocation());
                player.sendActionMessage("i�meta ginkl� kuris atrodo kaip " + weaponData.getWeaponData().model.modelName);
                player.removeWeapon(weaponData);
            }
        }
        return true;
    }

    public boolean grabGun(Player p) {
        LtrpPlayer player = playerPlugin.get(p);
        if(player == null)
            return false;

        if(player.isInComa())
            player.sendErrorMessage("J�s esate komos b�senoje.");
        else {
            Optional<DroppedWeaponData> optionalDropped = dropPlugin.getDroppedWeapos().stream()
                    .filter(w -> !w.isDestroyed() && w.getLocation().distance(player.getPlayer().getLocation()) < 5f)
                    .findFirst();
            if(!optionalDropped.isPresent())
                player.sendErrorMessage("Prie j�s� nesim�to joks ginklas.");
            else {
                DroppedWeaponData wep = optionalDropped.get();
                if(player.ownsWeapon(wep.getWeaponData().model))
                    player.sendErrorMessage("J�s jau turite tok� ginkl�.");
                else if(player.isWeaponSlotUsed(wep.getWeaponData().model.slot))
                    player.sendErrorMessage("J�s jau turite tokio tipo ginkl�.");
                else {
                    player.giveWeapon(new LtrpWeaponData(wep.getUUID(), wep.getWeaponData().model, wep.getWeaponData().ammo, false));
                    wep.destroy();
                    player.getPlayer().setArmedWeapon(wep.getWeaponData().model);
                }
            }
        }
        return true;
    }

}
