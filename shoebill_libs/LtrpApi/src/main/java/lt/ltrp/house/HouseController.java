package lt.ltrp.house;

import lt.ltrp.data.Color;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.18.
 */
public abstract class HouseController {


    private static HouseController instance;

    public static HouseController get() {
        return instance;
    }

    protected HouseController() {
        instance = this;
    }

    public abstract House create(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit,
                               Color labelColor, int money, int rentPrice);

    public abstract Collection<House> getAll();
    public abstract House get(Location location);
    public abstract void showManagementDialog(LtrpPlayer player);
    public abstract House getClosest(Location location, float maxDistance);
    public abstract void remove(House house);
    public abstract void update(House house);



}
