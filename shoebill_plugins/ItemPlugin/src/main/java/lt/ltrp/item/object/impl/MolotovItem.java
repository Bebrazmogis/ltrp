package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.object.Fire;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.Item;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.ItemUsageOption;
import lt.maze.mapandreas.MapAndreas;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Optional;
import java.util.Random;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class MolotovItem extends BasicItem {

    public MolotovItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.Molotov, false);
    }

    public MolotovItem(EventManager eventManager) {
        this(0, "Degus skystis", eventManager);
    }

    @ItemUsageOption(name = "Mesti")
    public boolean tHrow(LtrpPlayer player, Inventory inventory) {
        // Player cant use other inventories for this
        if(player.getInventory() != inventory) {
            return false;
        }

        // Molotovs are only allowed to use when an administrator is near.
        Optional<LtrpPlayer> optionalAdmin = LtrpPlayer.get().stream()
                .filter(LtrpPlayer::isAdmin)
                .findFirst();

        if(optionalAdmin.isPresent()) {
            Item item = inventory.getItem(ItemType.Lighter);
            if(item != null) {
                player.applyAnimation("GRENADE", "WEAPON_throw", 4.1f, 0, 1, 1, 0, 2000, 1);
                player.sendActionMessage("uþdega molotovo kokteilá ir meta já");

                final Location explosionLoc = new Location();
                float  distanceThrown = new Random().nextFloat()*20 + 8.0f;
                AngledLocation playerLoc = player.getLocation();

                explosionLoc.setX(playerLoc.getX() + (float)(distanceThrown * Math.sin(-Math.toRadians(playerLoc.getAngle()))));
                explosionLoc.setY(playerLoc.getY() + (float) (distanceThrown * Math.cos(-Math.toRadians(playerLoc.getAngle()))));
                LtrpPlayer.sendAdminMessage("Þaidëjas " + player.getName() + " meta molotov kokteilá, artimiausias administratorius " + optionalAdmin.get().getName());
                // We play the animation until we find the Z coordination asynchronously
                // Note2. Actually there's no need for this, MapAndreas is fast(at the time of code writing I thought it takes 2+ seconds)
                // Just leaving this to see how it looks in-game
                new Thread(() -> {
                    explosionLoc.setZ(MapAndreas.findZ(explosionLoc.getX(), explosionLoc.getY()));
                    // Once we get the Z coordinate we run explosion creation on SAMP thread
                    Shoebill.get().runOnSampThread(() -> {
                        Fire.create(player, explosionLoc, 6, 11, 1);
                        player.clearAnimations(1);
                    });
                }).start();
                return true;
            } else {
                player.sendErrorMessage("Neturite su kuo uþdegti molotov.");
            }
        } else {
            player.sendErrorMessage("Kad galëtumëte naudoti molotov kokteilá ðalia jûsø turi bûti administratorius.");
        }
        return false;
    }


}

