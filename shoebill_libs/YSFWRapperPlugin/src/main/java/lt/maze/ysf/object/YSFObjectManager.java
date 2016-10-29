package lt.maze.ysf.object;

import net.gtaun.shoebill.event.destroyable.DestroyEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.03.
 */
public final class YSFObjectManager implements Destroyable {

    private static YSFObjectManager instance;
    private static final Collection<YSFPlayerZone> playerZones = new ArrayList<>();
    private static final Collection<YSFObject> objects = new ArrayList<>();
    private static final Collection<YSFVehicle> vehicles = new ArrayList<>();
    private static final Collection<YSFZone> zones = new ArrayList<>();
    private static final Collection<YSFPickup> pickups = new ArrayList<>();

    public static YSFObjectManager getInstance() {
        return instance;
    }

    private EventManagerNode eventManagerNode;
    private boolean destroyed;

    public YSFObjectManager(EventManager eventManager) {
        instance = this;
        eventManagerNode = eventManager.createChildNode();

        eventManagerNode.registerHandler(DestroyEvent.class, e -> {
            Destroyable destroyable = e.getDestroyable();
            if(destroyable instanceof YSFPlayerZone && playerZones.contains(destroyable)) {
                playerZones.remove(destroyable);
            } else if(destroyable instanceof YSFObject && objects.contains(destroyable)) {
                objects.remove(destroyable);
            }
        });
    }

    public Collection<YSFObject> getObjects() {
        return objects;
    }


    protected Collection<YSFPlayerObject> getPlayerObjects() {
        Collection<YSFPlayerObject> ob = new ArrayList<>();
        objects.stream()
                .filter(o -> o instanceof YSFPlayerObject)
                .forEach(o -> ob.add((YSFPlayerObject)o));
        return ob;
    }

    protected Collection<YSFVehicle> getVehicles() {
        return vehicles;
    }

    protected Collection<YSFPlayerZone> getPlayerZones() {
        return playerZones;
    }

    protected Collection<YSFZone> getZones() {
        return zones;
    }

    protected Collection<YSFPickup> getPickups() {
        return pickups;
    }

    @Override
    public void destroy() {
        destroyed = true;
        eventManagerNode.cancelAll();
        playerZones.forEach(Destroyable::destroy);
        objects.forEach(Destroyable::destroy);

        playerZones.clear();
        objects.clear();
        vehicles.clear();
        zones.clear();
        pickups.clear();

    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
