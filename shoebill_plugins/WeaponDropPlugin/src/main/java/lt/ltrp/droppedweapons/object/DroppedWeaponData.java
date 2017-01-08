package lt.ltrp.droppedweapons.object;

import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.droppedweapons.event.WeaponDisappearEvent;
import lt.ltrp.droppedweapons.event.WeaponDropEvent;
import lt.maze.mapandreas.MapAndreas;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.entities.Destroyable;
import net.gtaun.shoebill.entities.Timer;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class DroppedWeaponData extends LtrpWeaponData implements Destroyable {

    private static List<DroppedWeaponData> droppedWeapons = new ArrayList<>();
    private static MapAndreas mapAndreasPlugin;

    @Deprecated
    public static List<DroppedWeaponData> getDroppedWeapons() {
        return (List<DroppedWeaponData>) get();
    }

    public static Collection<DroppedWeaponData> get() {
        // Ensures that there are no destroyed weapons in the returned collection
        droppedWeapons = droppedWeapons.stream().filter(w -> !w.isDestroyed()).collect(Collectors.toList());
        return droppedWeapons;
    }

    private static MapAndreas getMapAndreas() {
        if(mapAndreasPlugin == null) {
            mapAndreasPlugin = ResourceManager.get().getPlugin(MapAndreas.class);
        }
        return mapAndreasPlugin;
    }

    private LocalDateTime dropDate;
    private Location location;
    private DynamicObject object;
    private boolean destroyed;
    private Timer timer;
    private EventManager eventManager;



    public DroppedWeaponData(WeaponModel type, int ammo, boolean job, Location location, int disappearInterval, EventManager eventManager) {
        super(type, ammo, job);
        this.dropDate = LocalDateTime.now();
        this.eventManager = eventManager;
        new Thread(() -> {
            float z = getMapAndreas().findZ(location.x, location.y);
            // Taigi. Jei þmogus po tilto ar pnð, MapAndreas graþins VIRÐ tilto koordinates.
            // Todël jeigu jis duoda mums koordinates aukðèiau nei þaidëjo Z, atimam ið þaidëjo Z ðiektiek.
            if(z > location.z) {
                z = location.z;
            }
            this.location = new Location(location.x, location.y, z);
            Shoebill.get().runOnSampThread(() -> {
                this.object = DynamicObject.create(type.modelId, this.location, new Vector3D());
                timer = Timer.create(disappearInterval, 1, (interval) -> {
                    this.destroy();
                    this.eventManager.dispatchEvent(new WeaponDisappearEvent(this));
                });
                timer.start();
            });
        }).start();
        this.eventManager.dispatchEvent(new WeaponDropEvent(this));
        droppedWeapons.add(this);
    }

    public LocalDateTime getDropDate() {
        return dropDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    @Override
    protected void finalize() throws Throwable {
        if(!isDestroyed()) destroy();
        super.finalize();
    }

    @Override
    public void destroy() {
        destroyed = true;
        droppedWeapons.remove(this);
        if(timer.isRunning())
            timer.stop();
        object.destroy();
        this.dropDate = null;
        this.object = null;
        this.location = null;
        this.timer = null;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
