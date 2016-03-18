package lt.ltrp.vehicle;

import lt.ltrp.dao.VehicleDao;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleDamage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class PlayerVehicle extends LtrpVehicle {

    public static List<PlayerVehicle> get() {
        ArrayList<PlayerVehicle> a = new ArrayList<>();
        LtrpVehicle.get().stream().filter(v -> v instanceof PlayerVehicle).forEach(v -> a.add((PlayerVehicle)v));
        return a;
    }

    public static PlayerVehicle getById(int id) {
        for(LtrpVehicle veh : LtrpVehicle.get()) {
            if(veh instanceof PlayerVehicle && veh.getId() == id) {
                return (PlayerVehicle)veh;
            }
        }
        return null;
    }

    public static PlayerVehicle getByVehicle(Vehicle vehicle) {
        if(vehicle == null)
            return null;

        for(LtrpVehicle veh : LtrpVehicle.get()) {
            if(veh instanceof PlayerVehicle && veh.equals(vehicle)) {
                return (PlayerVehicle)veh;
            }
        }
        return null;
    }


    public static PlayerVehicle getByUniqueId(int uid) {
        for(LtrpVehicle veh : LtrpVehicle.get()) {
            if(veh instanceof PlayerVehicle && veh.getUniqueId() == uid) {
                return (PlayerVehicle)veh;
            }
        }
        return null;
    }

    public static PlayerVehicle getClosest(LtrpPlayer player, float distance) {
        return getClosest(player.getLocation(), distance);
    }
    public static PlayerVehicle getClosest(Location location, float distance) {
        PlayerVehicle vehicle = null;
        for(LtrpVehicle v : get()) {
            if(!(v instanceof PlayerVehicle))
                continue;
            float dis = location.distance(v.getLocation());
            if(dis < distance) {
                vehicle = (PlayerVehicle)v;
                distance = dis;
            }
        }
        return vehicle;
    }

    public static PlayerVehicle create(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId,
                                       int deaths, FuelTank fueltank, float mileage, String license, int insurance, VehicleAlarm alarm, VehicleLock lock, int doors, int panels,
                                       int lights, int tires, float health) {
        PlayerVehicle vehicle = new PlayerVehicle(id, modelId, location, color1, color2, ownerId, deaths, fueltank, mileage, license, insurance, alarm, lock, doors, panels, lights, tires, health);

        return vehicle;
    }


    private int ownerId, insurance;
    private VehicleLock lock;
    private VehicleAlarm alarm;
    private int deaths;
    private Map<Integer, Collection<PlayerVehiclePermission>> userPermissions;
    private float health;

    private PlayerVehicle(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId,
                          int deaths, FuelTank fueltank, float mileage, String license, int insurance, VehicleAlarm alarm,
                          VehicleLock lock, int doors, int panels, int lights, int tires, float health) {
        super(id, Vehicle.create(modelId, location, color1, color2, -1, false), fueltank, license, mileage);
        this.ownerId = ownerId;
        this.deaths = deaths;
        this.insurance = insurance;
        this.alarm = alarm;
        this.lock = lock;
        this.health = health;
        VehicleDamage dmg = getVehicleObject().getDamage();
        dmg.setDoors(doors);
        dmg.setPanels(panels);
        dmg.setLights(lights);
        dmg.setTires(tires);
        setHealth(health);
        this.userPermissions = new HashMap<>();
    }

    public void addPermission(int userId, PlayerVehiclePermission permission) {
        if(!userPermissions.containsKey(userId))
            userPermissions.put(userId, new ArrayList<>());
        userPermissions.get(userId).add(permission);
    }

    public void addPermission(LtrpPlayer player, PlayerVehiclePermission permission) {
        addPermission(player.getUserId(), permission);
    }

    /**
     * Returns the user permissions for this PlayerVehicle object
     * @param userId user ID whose permissions to get
     * @return a list of user permissions, if the user has no permission an empty collection is still returned
     */
    public Collection<PlayerVehiclePermission> getPermissions(int userId) {
        return userPermissions.containsKey(userId) ? userPermissions.get(userId) : new ArrayList<>(0);
    }

    public Map<Integer, Collection<PlayerVehiclePermission>> getPermissions() {
        return userPermissions;
    }

    public void removePermission(LtrpPlayer player, PlayerVehiclePermission permission) {
        removePermission(player.getUserId(), permission);
    }

    public void removePermission(int userId, PlayerVehiclePermission permission) {
        if(userPermissions.containsKey(userId)) {
            userPermissions.get(userId).remove(permission);
        }
    }

    public void removePermissions(int userId) {
        if(userPermissions.containsKey(userId)) {
            userPermissions.remove(userId);
        }
    }

    @Override
    public void setHealth(float health) {
        this.health = health;
        super.setHealth(health);
    }

    @Override
    public float getHealth() {
        if(getVehicleObject() != null) {
            health = super.getHealth();
        }
        return health;
    }

    public int getInsurance() {
        return insurance;
    }

    public void setInsurance(int insurance) {
        this.insurance = insurance;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public VehicleAlarm getAlarm() {
        return alarm;
    }

    public void setAlarm(VehicleAlarm alarm) {
        this.alarm = alarm;
    }

    public VehicleLock getLock() {
        return lock;
    }

    public void setLock(VehicleLock lock) {
        this.lock = lock;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void destroy() {
        if(getVehicleObject() != null) {
            super.destroy();
        }
    }
}
