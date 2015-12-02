package lt.ltrp.vehicle;

import lt.ltrp.item.Inventory;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModelInfoType;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.object.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class LtrpVehicle implements Vehicle {

    private static List<LtrpVehicle> vehicles = new ArrayList<>();

    public static LtrpVehicle getById(int id) {
        return (LtrpVehicle)Vehicle.get(id);
    }

    public static List<LtrpVehicle> get() {
        return vehicles;
    }

    public static LtrpVehicle getByUniqueId(int uniqueid) {
        for(LtrpVehicle v : vehicles) {
            if(v.getUid() == uniqueid)
                return v;
        }
        return null;
    }

    public static LtrpVehicle getClosest(LtrpPlayer player, float distance) {
        return getClosest(player.getLocation());
    }


    public static LtrpVehicle getClosest(Location loc, float distance) {
        LtrpVehicle vehicle = null;
        for(LtrpVehicle v : vehicles) {
            float dis = loc.distance(v.getLocation());
            if(dis < distance) {
                vehicle = v;
                distance = dis;
            }
        }
        return vehicle;
    }

    public static LtrpVehicle getClosest(Location loc) {
        return getClosest(loc, Float.MAX_VALUE);
    }

    private int uid;
    private FuelTank fuelTank;
    private boolean locked;
    private Vehicle vehicleObject;
    private Inventory inventory;

    protected LtrpVehicle() {

        setLocked(this.locked);
    }

    public FuelTank getFuelTank() {
        return fuelTank;
    }

    public void setFuelTank(FuelTank fuelTank) {
        this.fuelTank = fuelTank;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        this.getState().setDoors(locked ? VehicleParam.PARAM_ON : VehicleParam.PARAM_OFF);
    }

    public int getUid() {
        return uid;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void sendActionMessage(String s, float distance) {
        for(LtrpPlayer p : LtrpPlayer.get()) {
            if(p.isInVehicle(this) || p.getLocation().distance(this.getLocation()) <= distance) {
                p.sendMessage(lt.ltrp.data.Color.ACTION, "* "+ this.getModelName() + " " + s);
            }
        }
    }

    public void sendStateMessage(String s, float distance) {
        for(LtrpPlayer p : LtrpPlayer.get()) {
            if(p.isInVehicle(this) || p.getLocation().distance(this.getLocation()) <= distance) {
                p.sendMessage(lt.ltrp.data.Color.ACTION, "* " + s + " ((" + this.getModelName() + "))");
            }
        }
    }

    public void sendStateMessage(String s) {
        this.sendStateMessage(s, 3.0f);
    }

    public void sendActionMessage(String s) {
        this.sendActionMessage(s, 3.0f);
    }

    // Overrides from net.gtaun.shoebill.object.Vehicle interface

    @Override
    public boolean isStatic() {
        return vehicleObject.isStatic();
    }

    @Override
    public int getId() {
        return vehicleObject.getId();
    }

    @Override
    public int getModelId() {
        return vehicleObject.getModelId();
    }

    @Override
    public String getModelName() {
        return vehicleObject.getModelName();
    }

    @Override
    public int getColor1() {
        return vehicleObject.getColor1();
    }

    @Override
    public int getColor2() {
        return vehicleObject.getColor2();
    }

    @Override
    public int getRespawnDelay() {
        return vehicleObject.getRespawnDelay();
    }

    @Override
    public VehicleParam getState() {
        return vehicleObject.getState();
    }

    @Override
    public VehicleComponent getComponent() {
        return vehicleObject.getComponent();
    }

    @Override
    public VehicleDamage getDamage() {
        return vehicleObject.getDamage();
    }

    @Override
    public AngledLocation getLocation() {
        return vehicleObject.getLocation();
    }

    @Override
    public void setLocation(float v, float v1, float v2) {
        vehicleObject.setLocation(v, v1, v2);
    }

    @Override
    public void setLocation(Vector3D vector3D) {
        vehicleObject.setLocation(vector3D);
    }

    @Override
    public void setLocation(Location location) {
        vehicleObject.setLocation(location);
    }

    @Override
    public void setLocation(AngledLocation angledLocation) {
        vehicleObject.setLocation(angledLocation);
    }

    @Override
    public float getAngle() {
        return vehicleObject.getAngle();
    }

    @Override
    public void setAngle(float v) {
        vehicleObject.setAngle(v);
    }

    @Override
    public Quaternion getRotationQuat() {
        return vehicleObject.getRotationQuat();
    }

    @Override
    public int getInterior() {
        return vehicleObject.getInterior();
    }

    @Override
    public void setInterior(int i) {
        vehicleObject.setInterior(i);
    }

    @Override
    public int getWorld() {
        return vehicleObject.getWorld();
    }

    @Override
    public void setWorld(int i) {
        vehicleObject.setWorld(i);
    }

    @Override
    public float getHealth() {
        return vehicleObject.getHealth();
    }

    @Override
    public void setHealth(float v) {
        vehicleObject.setHealth(v);
    }

    @Override
    public Velocity getVelocity() {
        return vehicleObject.getVelocity();
    }

    @Override
    public void setVelocity(Velocity velocity) {
        vehicleObject.setVelocity(velocity);
    }

    @Override
    public void setAngularVelocity(Velocity velocity) {
        vehicleObject.setAngularVelocity(velocity);
    }


    public void putPlayer(LtrpPlayer player, int i) {
        putPlayer(player, i);
    }
    @Override
    public void putPlayer(Player player, int i) {
        vehicleObject.putPlayer(player, i);
    }


    public boolean isPlayerIn(LtrpPlayer player) {
        return isPlayerIn(player);
    }

    @Override
    public boolean isPlayerIn(Player player) {
        return vehicleObject.isPlayerIn(player);
    }


    public boolean isStreamedIn(LtrpPlayer player) {
        return isStreamedIn(player);
    }

    @Override
    public boolean isStreamedIn(Player player) {
        return vehicleObject.isStreamedIn(player);
    }

    @Override
    public void setParamsForPlayer(Player player, boolean b, boolean b1) {
        vehicleObject.setParamsForPlayer(player, b, b1);
    }

    @Override
    public void respawn() {
        vehicleObject.respawn();
    }

    @Override
    public void setColor(int i, int i1) {
        vehicleObject.setColor(i, i1);
    }

    @Override
    public void setPaintjob(int i) {
        vehicleObject.setPaintjob(i);
    }

    @Override
    public Vehicle getTrailer() {
        return vehicleObject.getTrailer();
    }

    @Override
    public void attachTrailer(Vehicle vehicle) {
        vehicleObject.attachTrailer(vehicle);
    }

    @Override
    public void detachTrailer() {
        vehicleObject.detachTrailer();
    }

    @Override
    public boolean isTrailerAttached() {
        return vehicleObject.isTrailerAttached();
    }

    @Override
    public void setNumberPlate(String s) {
        vehicleObject.setNumberPlate(s);
    }

    @Override
    public void repair() {
        vehicleObject.repair();
    }

    @Override
    public VehicleState getDoors() {
        return vehicleObject.getDoors();
    }

    @Override
    public VehicleState getWindows() {
        return vehicleObject.getWindows();
    }

    @Override
    public int getSirenState() {
        return vehicleObject.getSirenState();
    }

    @Override
    public void setDoors(VehicleState vehicleState) {
        vehicleObject.setDoors(vehicleState);
    }

    @Override
    public void setWindows(VehicleState vehicleState) {
        vehicleObject.setWindows(vehicleState);
    }

    @Override
    public boolean hasSiren() {
        return vehicleObject.hasSiren();
    }

    @Override
    public void destroy() {
        vehicleObject.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return vehicleObject.isDestroyed();
    }
}
