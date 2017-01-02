package lt.ltrp.garage;

import lt.ltrp.dao.GarageDao;
import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.19.
 */
public interface GarageController {

    Garage get(int uuid);
    Garage get(Location location);
    Garage getClosest(Location location, float maxDistance);
    GarageDao getDao();
    Collection<Garage> getGarages();
    void showManagementDialog(LtrpPlayer player);

    Garage createGarage(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, AngledLocation location, AngledLocation location1, Color labelColor);

    class Instance {
        static GarageController instance;
    }

    static GarageController get() {
        return Instance.instance;
    }
}
