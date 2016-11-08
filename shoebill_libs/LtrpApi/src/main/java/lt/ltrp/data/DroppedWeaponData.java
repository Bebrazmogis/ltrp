package lt.ltrp.data;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector2D;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class DroppedWeaponData extends LtrpWeaponData implements Destroyable{

    private static List<DroppedWeaponData> droppedWeapons = new ArrayList<>();

    private static final int DISAPPEAR_INTERVAL = 10*60*1000;

    public static List<DroppedWeaponData> getDroppedWeapons() {
        return droppedWeapons;
    }

    private Date dropDate;
    private Location location;
    private DynamicObject object;
    private boolean destroyed;
    private Timer timer;



    public DroppedWeaponData(WeaponModel type, int ammo, boolean job, Vector2D location) {
        super(type, ammo, job);
        this.dropDate = new GregorianCalendar().getTime();
        new Thread(() -> {
            //float z = MapAndreas.FindZ(location.getX(), location.getY());
            float z = 0f;
            // TODO
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


    public DroppedWeaponData(WeaponData data, boolean job, Vector2D location) {
        this(data.getModel(), data.getAmmo(), job, location);

    }

    public DroppedWeaponData(LtrpWeaponData data, Vector2D location) {
        this(data, data.isJob(), location);

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
