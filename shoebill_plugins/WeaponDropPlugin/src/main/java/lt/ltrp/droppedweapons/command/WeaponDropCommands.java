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
            player.sendErrorMessage("Jûs esate komos bûsenoje!");
        else if(player.getArmedWeaponData() == null || player.getArmedWeaponData().getWeaponData().model == WeaponModel.NONE)
            player.sendErrorMessage("Jûs nelaikote ginklo rankose!");
        else if(player.getPlayer().isInAnyVehicle())
            player.sendErrorMessage("Bûdamas transporto priemonëje iðmesti ginklø negalite.");
        else {
            LtrpWeaponData weaponData = player.getArmedWeaponData();
            // fail-safe
            if(weaponData == null)
                return false;
            if(weaponData.isJob())
                player.sendErrorMessage("Darbiniø ginklø mëtyti negalite");
            else {
                dropPlugin.dropWeapon(weaponData, player.getPlayer().getLocation());
                player.sendActionMessage("iðmeta ginklà kuris atrodo kaip " + weaponData.getWeaponData().model.modelName);
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
            player.sendErrorMessage("Jûs esate komos bûsenoje.");
        else {
            Optional<DroppedWeaponData> optionalDropped = dropPlugin.getDroppedWeapos().stream()
                    .filter(w -> !w.isDestroyed() && w.getLocation().distance(player.getPlayer().getLocation()) < 5f)
                    .findFirst();
            if(!optionalDropped.isPresent())
                player.sendErrorMessage("Prie jûsø nesimëto joks ginklas.");
            else {
                DroppedWeaponData wep = optionalDropped.get();
                if(player.ownsWeapon(wep.getWeaponData().model))
                    player.sendErrorMessage("Jûs jau turite toká ginklà.");
                else if(player.isWeaponSlotUsed(wep.getWeaponData().model.slot))
                    player.sendErrorMessage("Jûs jau turite tokio tipo ginklà.");
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
