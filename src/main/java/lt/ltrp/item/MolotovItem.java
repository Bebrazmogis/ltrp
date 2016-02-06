package lt.ltrp.item;

import lt.ltrp.object.Fire;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.plugin.mapandreas.MapAndreas;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Timer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class MolotovItem extends BasicItem {

    public MolotovItem(String name) {
        super(name, ItemType.Molotov, false);
    }

    public MolotovItem() {
        super("Degus skystis", ItemType.Molotov, false);
    }

    @ItemUsageOption(name = "Mesti")
    public boolean tHrow(LtrpPlayer player, Inventory inventory) {
        // Player cant use other inventories for this
        if(player.getInventory() != inventory) {
            return false;
        }

        LtrpPlayer[] closestPlayers = player.getClosestPlayers(5.0f);
        // Molotovs are only allowed to use when an administrator is near.
        LtrpPlayer admin = null;
        if(player.getAdminLevel() > 2)
            admin = player;
        else {
            for(LtrpPlayer p : closestPlayers) {
                if(p.getAdminLevel() > 2) {
                    admin = p;
                    break;
                }
            }
        }

        if(admin != null) {
            Item item = inventory.getItem(ItemType.Lighter);
            if(item != null) {
                player.applyAnimation("GRENADE", "WEAPON_throw", 4.1f, 0, 1, 1, 0, 2000, 1);
                player.sendActionMessage("uþdega molotovo kokteilá ir meta já");

                final Location explosionLoc = new Location();
                float  distanceThrown = new Random().nextFloat()*20 + 8.0f;
                AngledLocation playerLoc = player.getLocation();

                explosionLoc.setX(playerLoc.getX() + (float)(distanceThrown * Math.sin(-Math.toRadians(playerLoc.getAngle()))));
                explosionLoc.setY(playerLoc.getY() + (float) (distanceThrown * Math.cos(-Math.toRadians(playerLoc.getAngle()))));
                // We play the animation until we find the Z coordination asynchronously
                new Thread(() -> {
                    explosionLoc.setZ(MapAndreas.FindZ(explosionLoc.getX(), explosionLoc.getY()));
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

    protected static MolotovItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_basic WHERE id = ?";
        MolotovItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new MolotovItem(result.getString("name"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}

