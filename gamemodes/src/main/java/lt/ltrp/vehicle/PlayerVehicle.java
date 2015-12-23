package lt.ltrp.vehicle;

import lt.ltrp.dao.VehicleDao;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleDamage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class PlayerVehicle extends LtrpVehicle {

    public static PlayerVehicle getById(int id) {
        for(LtrpVehicle veh : LtrpVehicle.get()) {
            if(veh instanceof PlayerVehicle && veh.getId() == id) {
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
            if(!(v instanceof JobVehicle))
                continue;
            float dis = location.distance(v.getLocation());
            if(dis < distance) {
                vehicle = (PlayerVehicle)v;
                distance = dis;
            }
        }
        return vehicle;
    }



    private int ownerId, modelId, color1, color2, panels, lights, doors, tires, insurance;
    private VehicleLock lock;
    private VehicleAlarm alarm;
    private int deaths;
    private AngledLocation spawnLocation;
    private boolean spawned;


    public PlayerVehicle() {

    }

    public void spawn() {
        if(!spawned) {
            Vehicle vehicle = Vehicle.create(modelId, spawnLocation, color1, color2, -1, false);
            vehicle.setNumberPlate(getLicense());
            vehicle.getDamage().set(panels, doors, lights, tires);
            LtrpVehicle.get().add(this);
            this.setVehicleObject(vehicle);

            spawned = true;
        }
    }



    public void despawn() {
        if(spawned) {
            // This is to update stuff into my own values
            getDamage();

            getVehicleObject().destroy();
            setVehicleObject(null);

            LtrpVehicle.get().add(this);
            spawned = false;
        }
    }

    public void setDamageStatus(int panels, int doors, int lights, int tires) {
        this.panels = panels;
        this.doors = doors;
        this.lights = lights;
        this.tires = tires;
        if(getVehicleObject() != null) {
            getVehicleObject().getDamage().set(panels, doors, lights, tires);
        }
    }

    public int getTires() {
        return tires;
    }

    public int getDoorsDmg() {
        return doors;
    }

    public int getLights() {
        return lights;
    }

    public int getPanels() {
        return panels;
    }

    @Override
    public int getColor1() {
        return color1;
    }

    @Override
    public int getColor2() {
        return color2;
    }

    @Override
    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }


    @Override
    public VehicleDamage getDamage() {
        VehicleDamage dmg = super.getDamage();
        this.panels = dmg.getPanels();
        this.doors = dmg.getDoors();
        this.lights = dmg.getLights();
        this.tires = dmg.getTires();
        return dmg;
    }

    @Override
    public void setColor(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
        super.setColor(color1, color2);
    }

    public int getInsurance() {
        return insurance;
    }

    public void setInsurance(int insurance) {
        this.insurance = insurance;
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }

    public AngledLocation getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(AngledLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
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
}
