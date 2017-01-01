package lt.ltrp.object;

import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.entities.Destroyable;
import net.gtaun.shoebill.entities.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Bebras
 *         2015.12.03.
 *
 *         The idea is that these fires should only be possible to exsitnuish with a fire extinguisher weapon or a fire truck.
 *         Should be done using the GetPlayerCameraTargetObject functions. Streamer versions of those functions.
 *
 */
public class Fire implements Destroyable {

    private static final List<Fire> fires = new ArrayList<>();
    private static final int DEFAULT_FIRE_MODEL = 18690;

    public static List<Fire> get() {
        return fires;
    }

    public static Fire create(LtrpPlayer starter, Location center, int firecount, int explosiontype, int explosioncount) {
        Fire fire = new Fire(starter, explosiontype, center, firecount, explosioncount);
        fires.add(fire);
        return fire;
    }



    private int starterUserId;
    private int explosionType;
    private Location center;
    private int fireCount;
    private int explosionCount;
    private DynamicObject fireObjects[];
    private boolean destroyed;


    private Fire(LtrpPlayer starter, int explosionType, Location center, int fireCount, int explosionCount) {
        this.starterUserId = starter.getUUID();
        this.explosionType = explosionType;
        this.center = center;
        this.fireCount = fireCount;
        this.explosionCount = explosionCount;
        this.fireObjects = new DynamicObject[fireCount];

        if(explosionCount > 0) {
            starter.getPlayer().createExplosion(center, explosionType, 5.0f);
            this.explosionCount--;
        }
        Random random = new Random();
        for(int i = 0; i < fireCount; i++) {
            float offset = random.nextFloat() * 5;
            if(random.nextInt(2) == 1) {
                offset = -offset;
            }
            Location location = new Location(center);
            location.x = location.x + offset;
            location.y = location.y + offset;
            fireObjects[i] = DynamicObject.create(DEFAULT_FIRE_MODEL, location, new Vector3D());
        }

        if(explosionCount > 0) {
            Timer.create(600+ random.nextInt(500), explosionCount, (e) -> {
                starter.getPlayer().createExplosion(center, explosionType, 5.0f);
                this.explosionCount--;
            });
        }
    }


    public static List<Fire> getFires() {
        return fires;
    }

    public static int getDefaultFireModel() {
        return DEFAULT_FIRE_MODEL;
    }

    public int getStarterUserId() {
        return starterUserId;
    }

    public void setStarterUserId(int starterUserId) {
        this.starterUserId = starterUserId;
    }

    public int getExplosionType() {
        return explosionType;
    }

    public void setExplosionType(int explosionType) {
        this.explosionType = explosionType;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public int getFireCount() {
        return fireCount;
    }

    public void setFireCount(int fireCount) {
        this.fireCount = fireCount;
    }

    public int getExplosionCount() {
        return explosionCount;
    }

    public void setExplosionCount(int explosionCount) {
        this.explosionCount = explosionCount;
    }

    @Override
    public void destroy() {
        destroyed = true;
        for(int i = 0; i < fireObjects.length; i++) {
            if(fireObjects[i] != null) {
                fireObjects[i].destroy();
                fireObjects[i] = null;
            }
        }
        center = null;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
