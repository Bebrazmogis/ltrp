package lt.ltrp.data;

import lt.maze.mapandreas.MapAndreas;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class DroppedWeaponData extends LtrpWeaponData implements Destroyable{

    private static List<DroppedWeaponData> droppedWeapons = new ArrayList<>();

    private static final int DISAPPEAR_INTERVAL = 10*60*1000;

    public static List<DroppedWeaponData> getDroppedWeapons() {
        // Ensures that there are no destroyed weapons in the returned collection
        droppedWeapons = droppedWeapons.stream().filter(w -> !w.isDestroyed()).collect(Collectors.toList());
        return droppedWeapons;
    }

    private Date dropDate;
    private Location location;
    private DynamicObject object;
    private boolean destroyed;
    private Timer timer;



    public DroppedWeaponData(WeaponModel type, int ammo, boolean job, Location location) {
        super(type, ammo, job);
        this.dropDate = new GregorianCalendar().getTime();
        new Thread(() -> {
            float z = MapAndreas.findZ(location.x, location.y);
            // Taigi. Jei þmogus po tilto ar pnð, MapAndreas graþins VIRÐ tilto koordinates.
            // Todël jeigu jis duoda mums koordinates aukðèiau nei þaidëjo Z, atimam ið þaidëjo Z ðiektiek.
            if(z > location.z) {
                z = location.z;
            }
            this.location = new Location(location.getX(), location.getY(), z);
            Shoebill.get().runOnSampThread(() -> {
                this.object = DynamicObject.create(type.getModelId(), this.location, new Vector3D());
                timer = Timer.create(DISAPPEAR_INTERVAL, 1, (interval) -> {
                    this.destroy();
                });
                timer.start();
            });
        }).start();
        droppedWeapons.add(this);

    }

    public Date getDropDate() {
        return dropDate;
    }

    public void setDropDate(Date dropDate) {
        this.dropDate = dropDate;
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
