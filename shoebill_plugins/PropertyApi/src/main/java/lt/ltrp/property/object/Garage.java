package lt.ltrp.property.object;

import lt.ltrp.item.object.InventoryEntity;
import lt.ltrp.property.PropertyController;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface Garage extends Property, InventoryEntity {

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

    public static Garage create(int uniqueid, String name, EventManager eventManager1) {
        return create(uniqueid, name, null, null, eventManager1);
    }

    public static Garage create(int uniqueid, String name, Location entrance, Location exit, EventManager eventM) {
        return PropertyController.get().createGarage(uniqueid, name, entrance, exit, eventM);
    }


}
