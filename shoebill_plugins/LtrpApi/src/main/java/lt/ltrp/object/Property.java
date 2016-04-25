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

    static Property getClosest(Location location, float maxDistance) {
        Optional<Property> op = get().stream().min((b1, b2) -> {
            return Float.compare(Math.min(b1.getEntrance().distance(location), b1.getExit().distance(location)),
                    Math.min(b2.getEntrance().distance(location), b2.getExit().distance(location)));
        });
        if(op.isPresent()) {
            float distance = op.get().getEntrance().distance(location);
            if(distance <= maxDistance) {
                return op.get();
            }
        }
        return null;
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
