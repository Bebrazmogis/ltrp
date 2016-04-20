package lt.ltrp.object;

import lt.ltrp.PropertyController;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface Garage extends Property, InventoryEntity {

    static final Color DEFAULT_HOUSE_LABEL_COLOR = Color.WHITE;

    static Collection<Garage> get() {
        return PropertyController.get().getGarages();
    }

    static Garage get(int id) {
        Optional<Garage> op = get()
                .stream()
                .filter(b -> b.getUUID() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    static Garage create(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Location vehicleEntrance, Location vehicleExit,
                         Color labelColor, EventManager eventManager) {
        return PropertyController.get().createGarage(id, name, ownerUserId, pickupModelId, price, entrance, exit, vehicleEntrance, vehicleExit, labelColor, eventManager);
    }

    static Garage create(int id, Location entrance, Location exit, Location vehicleEntrance, Location vehicleExit, int price, EventManager eventManager1) {
        return create(id, "", LtrpPlayer.INVALID_USER_ID, -1, price, entrance, exit, vehicleEntrance, vehicleExit, DEFAULT_HOUSE_LABEL_COLOR, eventManager1);
    }

    void setVehicle(LtrpVehicle vehicle);
    LtrpVehicle getVehicle();

}
