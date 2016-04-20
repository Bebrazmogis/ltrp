package lt.ltrp.object;

import lt.ltrp.NamedEntity;
import lt.ltrp.PropertyController;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Destroyable;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface Property extends NamedEntity, Destroyable {

    static Collection<Property> get() {
        return PropertyController.get().getProperties();
    }

    static Property get(int id) {
        Optional<Property> op = get()
                .stream()
                .filter(b -> b.getUUID() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    int getOwner();
    void setOwner(int ownerUserId);
    Location getExit();
    void setExit(Location exit);
    Location getEntrance();
    void setEntrance(Location entrance);
    void sendActionMessage(String s);
    void sendStateMessage(String s);
    boolean isOwner(LtrpPlayer player);
    boolean isLocked();
    void setLocked(boolean set);
    int getPrice();
    void setPrice(int price);


}
