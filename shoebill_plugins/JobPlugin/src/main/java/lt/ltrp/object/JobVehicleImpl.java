package lt.ltrp.object;

import lt.ltrp.data.FuelTank;
import lt.ltrp.data.TaxiFare;
import lt.ltrp.data.VehicleRadio;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.object.*;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class JobVehicleImpl implements JobVehicle {

    public static JobVehicle getById(int id) {
       /* for(LtrpVehicle veh : JobVehicle.get()) {
            if(veh instanceof JobVehicle && veh.getId() == id) {
                return (JobVehicle)veh;
            }
        }
        return null;
       */
        return null;
    }

    public static JobVehicle getClosest(LtrpPlayer player, float distance) {
        return getClosest(player.getLocation(), distance);
    }

    @Override
    public FuelTank getFuelTank() {
        return null;
    }

    @Override
    public void setFuelTank(FuelTank fuelTank) {

    }

    @Override
    public AngledLocation getSpawnLocation() {
        return null;
    }

    @Override
    public void setSpawnLocation(AngledLocation angledLocation) {

    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public void setLocked(boolean b) {

    }

    @Override
    public float getMileage() {
        return 0;
    }

    @Override
    public void setMileage(float v) {

    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public String getLicense() {
        return null;
    }

    @Override
    public void setLicense(String s) {

    }

    @Override
    public void sendActionMessage(String s, float v) {

    }

    @Override
    public void sendStateMessage(String s, float v) {

    }

    @Override
    public void sendStateMessage(String s) {

    }

    @Override
    public void sendActionMessage(String s) {

    }

    @Override
    public boolean isUsed() {
        return false;
    }

    @Override
    public VehicleRadio getRadio() {
        return null;
    }

    @Override
    public LtrpPlayer getDriver() {
        return null;
    }

    @Override
    public void setDriver(LtrpPlayer ltrpPlayer) {

    }

    @Override
    public TaxiFare getTaxi() {
        return null;
    }

    public static JobVehicle getClosest(Location location, float distance) {
        JobVehicle vehicle = null;
       /* for(LtrpVehicle v : get()) {
            if(!(v instanceof JobVehicle))
                continue;
            float dis = location.distance(v.getLocation());
            if(dis < distance) {
                vehicle = (JobVehicle)v;
                distance = dis;
            }
        }*/
        return vehicle;
    }

    public static JobVehicle create(lt.ltrp.object.Job job, int modelid, AngledLocation location, int color1, int color2, lt.ltrp.object.Rank requiredrank, float mileage) {
        return create(0, job, modelid, location, color1, color2, requiredrank, job.getName().substring(0, 3) + modelid, mileage);
    }

    public static JobVehicle create(int id, lt.ltrp.object.Job job, int modelid, AngledLocation location, int color1, int color2, lt.ltrp.object.Rank requiredrank, String license, float mileage) {
        JobVehicle veh =  new JobVehicleImpl(id, job, modelid, location, color1, color2, requiredrank, license, mileage);
        //logger.debug("Creating job vehicle  " + veh.getId());
        return veh;
    }



    private lt.ltrp.object.Job job;
    private lt.ltrp.object.Rank rankNeeded;


    private JobVehicleImpl(lt.ltrp.object.Job job, int modelid, AngledLocation location, int color1, int color2, lt.ltrp.object.Rank requiredrank, String license, float mileage) {
        this(0, job, modelid, location, color1, color2, requiredrank, license, mileage);
    }

    private JobVehicleImpl(int id, lt.ltrp.object.Job job, int modelid, AngledLocation spawnLocation, int color1, int color2, lt.ltrp.object.Rank requiredrank, String license, float mileage) {
        //super(id, modelid, spawnLocation, color1, color2, license, mileage);
        this.job = job;
        this.rankNeeded = requiredrank;
    }

    public lt.ltrp.object.Rank getRequiredRank() {
        return rankNeeded;
    }

    public void setRequiredRank(lt.ltrp.object.Rank rankNeeded) {
        this.rankNeeded = rankNeeded;
    }

    public lt.ltrp.object.Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public void setInventory(Inventory inventory) {

    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public void setUUID(int i) {

    }

    @Override
    public int getUUID() {
        return 0;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int getModelId() {
        return 0;
    }

    @Override
    public String getModelName() {
        return null;
    }

    @Override
    public int getColor1() {
        return 0;
    }

    @Override
    public int getColor2() {
        return 0;
    }

    @Override
    public int getRespawnDelay() {
        return 0;
    }

    @Override
    public VehicleParam getState() {
        return null;
    }

    @Override
    public VehicleComponent getComponent() {
        return null;
    }

    @Override
    public VehicleDamage getDamage() {
        return null;
    }

    @Override
    public AngledLocation getLocation() {
        return null;
    }

    @Override
    public void setLocation(float v, float v1, float v2) {

    }

    @Override
    public void setLocation(Vector3D vector3D) {

    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public void setLocation(AngledLocation angledLocation) {

    }

    @Override
    public float getAngle() {
        return 0;
    }

    @Override
    public void setAngle(float v) {

    }

    @Override
    public Quaternion getRotationQuat() {
        return null;
    }

    @Override
    public int getInterior() {
        return 0;
    }

    @Override
    public void setInterior(int i) {

    }

    @Override
    public int getWorld() {
        return 0;
    }

    @Override
    public void setWorld(int i) {

    }

    @Override
    public float getHealth() {
        return 0;
    }

    @Override
    public void setHealth(float v) {

    }

    @Override
    public Velocity getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(Velocity velocity) {

    }

    @Override
    public void setAngularVelocity(Velocity velocity) {

    }

    @Override
    public void putPlayer(Player player, int i) {

    }

    @Override
    public boolean isPlayerIn(Player player) {
        return false;
    }

    @Override
    public boolean isStreamedIn(Player player) {
        return false;
    }

    @Override
    public void setParamsForPlayer(Player player, boolean b, boolean b1) {

    }

    @Override
    public void respawn() {

    }

    @Override
    public void setColor(int i, int i1) {

    }

    @Override
    public void setPaintjob(int i) {

    }

    @Override
    public Vehicle getTrailer() {
        return null;
    }

    @Override
    public void attachTrailer(Vehicle vehicle) {

    }

    @Override
    public void detachTrailer() {

    }

    @Override
    public boolean isTrailerAttached() {
        return false;
    }

    @Override
    public void setNumberPlate(String s) {

    }

    @Override
    public void repair() {

    }

    @Override
    public VehicleState getDoors() {
        return null;
    }

    @Override
    public VehicleState getWindows() {
        return null;
    }

    @Override
    public int getSirenState() {
        return 0;
    }

    @Override
    public void setDoors(VehicleState vehicleState) {

    }

    @Override
    public void setWindows(VehicleState vehicleState) {

    }

    @Override
    public boolean hasSiren() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isDestroyed() {
        return false;
    }
}
