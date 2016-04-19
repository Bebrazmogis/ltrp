package lt.ltrp.object.impl;

import lt.ltrp.InventoryEntityImpl;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.FuelTank;
import lt.ltrp.data.TaxiFare;
import lt.ltrp.data.VehicleRadio;
import lt.ltrp.event.vehicle.VehicleDestroyEvent;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.*;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class LtrpVehicleImpl extends InventoryEntityImpl implements LtrpVehicle {

    public static final float SPEED_MAGIC_NUMBER = 170f;
    protected static final Logger logger = LoggerFactory.getLogger(LtrpVehicle.class);


    private EventManager eventManager;
    private FuelTank fuelTank;
    private boolean locked;
    private Vehicle vehicleObject;
    private String license;
    private float mileage;
    private LtrpPlayer driver;
    private TaxiFare taxi;
    private AngledLocation spawnLocation;
    private VehicleRadio radio;

    public LtrpVehicleImpl(int id, int modelId, AngledLocation location, int color1, int color2, FuelTank fueltank, String license, float mileage, EventManager eventManager) {
        super(id,
                VehicleModel.getName(modelId) + " " + license,
                null);
        vehicleObject = Vehicle.create(modelId, location, color1, color2, -1, false);
        if(vehicleObject == null)
            throw new CreationFailedException("LtrpVehicle could not be created");
        this.eventManager = eventManager;
        this.fuelTank = fueltank;
        this.mileage = mileage;
        this.spawnLocation = location;
        if(LtrpVehicleModel.HasNumberPlates(modelId))
            vehicleObject.setNumberPlate(license);
        this.license = license;
        setLocked(this.locked);
        setInventory(Inventory.create(eventManager, this, VehicleModel.getName(modelId) + " bagaþinës", 20));
        this.radio = new VehicleRadio(this, eventManager);
    }


    public Vehicle getVehicleObject() {
        return vehicleObject;
    }

    public FuelTank getFuelTank() {
        return fuelTank;
    }

    public void setFuelTank(FuelTank fuelTank) {
        this.fuelTank = fuelTank;
    }

    public TaxiFare getTaxi() {
        return taxi;
    }

    public void setTaxi(TaxiFare taxi) {
        this.taxi = taxi;
    }

    public AngledLocation getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(AngledLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        this.getState().setDoors(locked ? VehicleParam.PARAM_ON : VehicleParam.PARAM_OFF);
    }

    public float getMileage() {
        return mileage;
    }

    public void setMileage(float mileage) {
        this.mileage = mileage;
    }

    public int getSpeed() {
        //floatround( floatsqroot( x*x + y*y + z*z ) * 170 );
        return Math.round(getVelocity().speed3d() * SPEED_MAGIC_NUMBER);
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
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

    public boolean isUsed() {
        for(LtrpPlayer player : LtrpPlayer.get()) {
            if(player.isInAnyVehicle() && player.getVehicle().equals(this)) {
                return true;
            }
        }
        return false;
    }

    public VehicleRadio getRadio() {
        return radio;
    }

    public LtrpPlayer getDriver() {
        return driver;
    }

    public void setDriver(LtrpPlayer player) {
        this.driver = player;
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
        vehicleObject.putPlayer(player, i);
    }

    public void putPlayer(LtrpPlayer player) {
        List<LtrpPlayer> passengers = LtrpPlayer.get().stream().filter(p -> p.isInVehicle(this)).collect(Collectors.toList());
        int seatCount = VehicleModel.getSeats(getModelId());
        if(passengers.size() < seatCount) {
            for(int i = 0; i < seatCount; i++) {
                boolean used = false;
                for(LtrpPlayer p : passengers) {
                    if(p.getVehicleSeat() == i) {
                        used = true;
                        break;
                    }
                }
                if(!used) {
                    putPlayer(player, i);
                    break;
                }
            }
        }
    }

    @Override
    public void putPlayer(Player player, int i) {
        vehicleObject.putPlayer(player, i);
    }


    public boolean isPlayerIn(LtrpPlayer player) {
        return vehicleObject.isPlayerIn(player);
    }

    @Override
    public boolean isPlayerIn(Player player) {
        return vehicleObject.isPlayerIn(player);
    }


    public boolean isStreamedIn(LtrpPlayer player) {
        return vehicleObject.isStreamedIn(player);
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
        eventManager.dispatchEvent(new VehicleDestroyEvent(this));
        vehicleObject.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return vehicleObject.isDestroyed();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LtrpVehicle && ((LtrpVehicle) obj).getUUID() == getUUID();
    }
}
