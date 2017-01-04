package lt.ltrp.player.vehicle.object.impl;

import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission;
import lt.ltrp.data.VehicleLock;
import lt.ltrp.player.vehicle.event.PlayerVehicleDestroyEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
import lt.ltrp.player.vehicle.object.VehicleAlarm;
import lt.ltrp.vehicle.object.impl.LtrpVehicleImpl;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class PlayerVehicleImpl extends LtrpVehicleImpl implements PlayerVehicle{
/*
    public static List<PlayerVehicle> get() {
        return (List)PlayerVehicleManager.playerVehiclesList;
    }

    public static PlayerVehicle getById(int id) {
        Optional<PlayerVehicle> optional = get()
                .stream()
                .filter(v -> v.getId() == id)
                .findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

*/
    private int ownerId, insurance;
    private VehicleLock lock;
    private VehicleAlarm alarm;
    private int deaths;
    private Map<Integer, Collection<PlayerVehiclePermission>> userPermissions;
    private float health;

    public PlayerVehicleImpl(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId,
                          int deaths, FuelTank fueltank, float mileage, String license, int insurance, VehicleAlarm alarm,
                          VehicleLock lock, int doors, int panels, int lights, int tires, float health, EventManager eventManager) {
        super(id, modelId, location, color1, color2, fueltank, license, mileage, eventManager);
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
        addPermission(player.getUUID(), permission);
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
        removePermission(player.getUUID(), permission);
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
        super.destroy();
        if(!isDestroyed())
            getEventManager().dispatchEvent(new PlayerVehicleDestroyEvent(this));
    }

    @Override
    public String toString() {
        return String.format("%s: %d %s location:%s", getClass().getName(), getUUID(), getName(), getLocation());
    }
}
