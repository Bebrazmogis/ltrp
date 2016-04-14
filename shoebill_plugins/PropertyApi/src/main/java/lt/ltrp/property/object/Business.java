package lt.ltrp.property.object;

import lt.ltrp.property.PropertyController;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface Business extends Property {

    static Collection<Business> get() {
        return PropertyController.get().getBusinesses();
    }

    static Business get(int id) {
        Optional<Business> op = get()
                .stream()
                .filter(b -> b.getUUID() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public static Business create(int uniqueid, String name, EventManager eventManager1) {
        return create(uniqueid, name, null, null, eventManager1);
    }

    public static Business create(int uniqueid, String name, Location entrance, Location exit, EventManager eventM) {
        return PropertyController.get().createBusiness(uniqueid, name, entrance, exit, eventM);
    }


}
