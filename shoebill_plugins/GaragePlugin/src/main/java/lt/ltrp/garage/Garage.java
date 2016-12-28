package lt.ltrp.garage;

import lt.ltrp.GarageController;
import lt.ltrp.object.Entity;import lt.ltrp.object.InventoryEntity;import lt.ltrp.object.LtrpPlayer;import lt.ltrp.object.LtrpVehicle;import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface Garage extends Property, InventoryEntity {

    static final Color DEFAULT_GARAGE_LABEL_COLOR = Color.WHITE;
    static final int DEFAULT_PICKUP_MODEL = 19522;
    static final int MIN_NAME_LENGTH = 5;

    static Collection<Garage> get() {
        return GarageController.get().getGarages();
    }

    static Garage get(int id) {
        return GarageController.get().get(id);
    }

    static Garage get(Location location) {
        return GarageController.get().get(location);
    }

    static Garage get(LtrpPlayer player) {
        return get(player.getLocation());
    }

    static Garage getClosest(LtrpPlayer player, float maxDistance) {
        return getClosest(player.getLocation(), maxDistance);
    }
    static Garage getClosest(Location location, float maxDistance) {
        return GarageController.get().getClosest(location, maxDistance);
    }

    static Garage create(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, AngledLocation vehicleEntrance, AngledLocation vehicleExit,
                         Color labelColor) {
        return GarageController.get().createGarage(id, name, ownerUserId, pickupModelId, price, entrance, exit, vehicleEntrance, vehicleExit, labelColor);
    }

    static Garage create(int id, Location entrance, Location exit, AngledLocation vehicleEntrance, AngledLocation vehicleExit, int price) {
        return create(id, "", Entity.Companion.getINVALID_ID(), DEFAULT_PICKUP_MODEL, price, entrance, exit, vehicleEntrance, vehicleExit, DEFAULT_GARAGE_LABEL_COLOR);
    }

    void setVehicle(LtrpVehicle vehicle);
    LtrpVehicle getVehicle();
    AngledLocation getVehicleEntrance();
    AngledLocation getVehicleExit();
    void setVehicleEntrance(AngledLocation location);
    void setVehicleExit(AngledLocation location);

}
