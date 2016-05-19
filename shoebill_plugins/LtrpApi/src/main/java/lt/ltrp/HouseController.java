package lt.ltrp;

import lt.ltrp.dao.HouseDao;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.18.
 */
public interface HouseController {

    House createHouse(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit,
                      lt.ltrp.data.Color labelColor, int money, int rentprice);
    Collection<House> getHouses();
    HouseDao getHouseDao();


    House getHouse(Location location);

    void showManagementDialog(LtrpPlayer player);

    House getClosest(Location location, float maxDistance);

    class Instance {
        static HouseController instance;
    }

    static HouseController get() {
        return Instance.instance;
    }
}
