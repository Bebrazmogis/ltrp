package lt.maze.ysf.object;

import lt.maze.ysf.object.impl.YSFVehicleImpl;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.03.
 */
public interface YSFVehicle {

    AngledLocation getSpawnLocation();
    void setSpawnInfo(int modelId, AngledLocation location, int color1, int color2, int respawntime, int interior);
    int getSpawnColor1();
    int getSpawnColor2();
    int getPaintjob();
    int getInterior();
    String getLicense();
    void setRespawnDelay(int delay);
    int getRespawnDelay();
    void setOccupiedTick(int ticks);
    int getOccupiedTick();
    void setRespawnTick(int ticks);
    int getRespawnTick();
    Player getLastDriver();
    Vehicle getCab();
    boolean hasBeenOccupied();
    void setOccupied(boolean set);
    boolean isOccupied();
    boolean isDead();
    int getColor1();
    int getColor2();

    default void setSpawnInfo(int modelId, AngledLocation location, int color1, int color2) {
        this.setSpawnInfo(modelId, location, color1, color2, -2, -2);
    }

    public static Collection<YSFVehicle> get() {
        return YSFObjectManager.getInstance().getVehicles();
    }

    public static YSFVehicle get(int id) {
        Optional<YSFVehicle> op = YSFObjectManager.getInstance().getVehicles()
                .stream()
                .filter(v -> v instanceof YSFVehicleImpl && ((YSFVehicleImpl)v).getVehicle().getId() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public static YSFVehicle get(Vehicle vehicle) {
        Optional<YSFVehicle> op = YSFObjectManager.getInstance().getVehicles()
                .stream()
                .filter(v -> v instanceof YSFVehicleImpl && ((YSFVehicleImpl) v).getVehicle().equals(vehicle))
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }


}

/*
// Vehicle functions
native GetVehicleSpawnInfo(vehicleid, &Float:fX, &Float:fY, &Float:fZ, &Float:fRot, &color1, &color2);
native SetVehicleSpawnInfo(vehicleid, modelid, Float:fX, Float:fY, Float:fZ, Float:fAngle, color1, color2, respawntime = -2, interior = -2);
native GetVehiclePaintjob(vehicleid);
native GetVehicleInterior(vehicleid);
native GetVehicleNumberPlate(vehicleid, plate[], len = sizeof(plate));
native SetVehicleRespawnDelay(vehicleid, delay);
native GetVehicleRespawnDelay(vehicleid);
native SetVehicleOccupiedTick(vehicleid, ticks);
native GetVehicleOccupiedTick(vehicleid); // GetTickCount() - GetVehicleOccupiedTick(vehicleid) = time passed since vehicle is occupied, in ms
native SetVehicleRespawnTick(vehicleid, ticks);
native GetVehicleRespawnTick(vehicleid); // GetTickCount() - GetVehicleRespawnTick(vehicleid) = time passed since vehicle spawned, in ms
native GetVehicleLastDriver(vehicleid);
native GetVehicleCab(vehicleid);
native HasVehicleBeenOccupied(vehicleid);
native SetVehicleBeenOccupied(vehicleid, occupied);
native IsVehicleOccupied(vehicleid);
native IsVehicleDead(vehicleid);
 */
