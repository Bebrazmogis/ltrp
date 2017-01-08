package lt.maze.ysf.object.impl;

import lt.maze.ysf.Functions;
import lt.maze.ysf.object.YSFVehicle;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.amx.types.ReferenceInt;
import net.gtaun.shoebill.amx.types.ReferenceString;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.object.Vehicle;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFVehicleImpl implements YSFVehicle {

    private Vehicle vehicle;
    private AngledLocation spawnLocation;
    private int spawnColor1, spawnColor2;

    public YSFVehicleImpl(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /*

native SetVehicleSpawnInfo(vehicleid, modelid, Float:fX, Float:fY, Float:fZ, Float:fAngle, color1, color2, respawntime = -2, interior = -2);

     */

    // native GetVehicleSpawnInfo(vehicleid, &Float:fX, &Float:fY, &Float:fZ, &Float:fRot, &color1, &color2);
    private void getSpawnInfo() {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        ReferenceFloat angle = new ReferenceFloat(0f);
        ReferenceInt color1 = new ReferenceInt(0);
        ReferenceInt color2 = new ReferenceInt(0);
        Functions.GetVehicleSpawnInfo(vehicle.getId(), x, y, z, angle, color1, color2);
        spawnLocation = new AngledLocation(x.getValue(), y.getValue(), z.getValue(), angle.getValue());
        spawnColor1 = color1.getValue();
        spawnColor2 = color2.getValue();
    }

    @Override
    public AngledLocation getSpawnLocation() {
        getSpawnInfo();
        return spawnLocation;
    }

    @Override
    public void setSpawnInfo(int modelId, AngledLocation location, int color1, int color2, int respawntime, int interior) {
        Functions.SetVehicleSpawnInfo(vehicle.getId(), modelId, location.x, location.y, location.z, location.angle, color1, color2, respawntime, interior);
    }

    @Override
    public int getSpawnColor1() {
        getSpawnInfo();
        return spawnColor1;
    }

    @Override
    public int getSpawnColor2() {
        getSpawnInfo();
        return spawnColor2;
    }

    //native GetVehiclePaintjob(vehicleid);
    @Override
    public int getPaintjob() {
        return Functions.GetVehiclePaintjob(vehicle.getId());
    }

    //native GetVehicleInterior(vehicleid);
    @Override
    public int getInterior() {
        return Functions.GetVehicleInterior(vehicle.getId());
    }

    //native GetVehicleNumberPlate(vehicleid, plate[], len = sizeof(plate));
    @Override
    public String getLicense() {
        ReferenceString s = new ReferenceString("", 16);
        Functions.GetVehicleNumberPlate(vehicle.getId(), s, s.getLength());
        return s.getValue();
    }

    //native SetVehicleRespawnDelay(vehicleid, delay);
    @Override
    public void setRespawnDelay(int delay) {
        Functions.SetVehicleRespawnDelay(vehicle.getId(), delay);
    }

    //    native GetVehicleRespawnDelay(vehicleid);
    @Override
    public int getRespawnDelay() {
        return Functions.GetVehicleRespawnDelay(vehicle.getId());
    }

    //native SetVehicleOccupiedTick(vehicleid, ticks);
    @Override
    public void setOccupiedTick(int ticks) {
        Functions.SetVehicleOccupiedTick(vehicle.getId(), ticks);
    }

    //native GetVehicleOccupiedTick(vehicleid); // GetTickCount() - GetVehicleOccupiedTick(vehicleid) = time passed since vehicle is occupied, in ms
    @Override
    public int getOccupiedTick() {
        return Functions.GetVehicleOccupiedTick(vehicle.getId());
    }

    //native SetVehicleRespawnTick(vehicleid, ticks);
    @Override
    public void setRespawnTick(int ticks) {
        Functions.SetVehicleRespawnTick(vehicle.getId(), ticks);
    }

    //native GetVehicleRespawnTick(vehicleid); // GetTickCount() - GetVehicleRespawnTick(vehicleid) = time passed since vehicle spawned, in ms
    @Override
    public int getRespawnTick() {
        return Functions.GetVehicleRespawnTick(vehicle.getId());
    }

    //native GetVehicleLastDriver(vehicleid);
    @Override
    public Player getLastDriver() {
        return Player.get(Functions.GetVehicleLastDriver(vehicle.getId()));
    }

    //native GetVehicleCab(vehicleid);
    @Override
    public Vehicle getCab() {
        return Vehicle.get(Functions.GetVehicleCab(vehicle.getId()));
    }

    //native HasVehicleBeenOccupied(vehicleid);
    @Override
    public boolean hasBeenOccupied() {
        return Functions.HasVehicleBeenOccupied(vehicle.getId()) != 0;
    }

    //native SetVehicleBeenOccupied(vehicleid, occupied);
    @Override
    public void setOccupied(boolean set) {
        Functions.SetVehicleBeenOccupied(vehicle.getId(), set ? 1 : 0);
    }

    //native IsVehicleOccupied(vehicleid);
    @Override
    public boolean isOccupied() {
        return Functions.IsVehicleOccupied(vehicle.getId()) != 0;
    }

    //native IsVehicleDead(vehicleid);
    @Override
    public boolean isDead() {
        return Functions.IsVehicleDead(vehicle.getId()) != 0;
    }

    //native GetVehicleColor(vehicleid, &color1, &color2);
    @Override
    public int getColor1() {
        ReferenceInt color1 = new ReferenceInt(0);
        ReferenceInt color2 = new ReferenceInt(0);
        Functions.GetVehicleColor(vehicle.getId(), color1, color2);
        return color1.getValue();
    }

    @Override
    public int getColor2() {
        ReferenceInt color1 = new ReferenceInt(0);
        ReferenceInt color2 = new ReferenceInt(0);
        Functions.GetVehicleColor(vehicle.getId(), color1, color2);
        return color2.getValue();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
