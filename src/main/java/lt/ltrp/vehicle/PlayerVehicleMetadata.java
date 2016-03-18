package lt.ltrp.vehicle;

/**
 * @author Bebras
 *         2016.03.18.
 *
 *         This class has but one purpose: to be a DTO between DB and views.
 */
public class PlayerVehicleMetadata {

    private int id;
    private int modelId;
    private int deaths;
    private float fuel;
    private int ownerId;
    private String license;
    private VehicleAlarm alarm;
    private VehicleLock lock;
    private int insurance;


    public PlayerVehicleMetadata(int id, int modelId, int deaths, float fuel, int ownerId, String license, VehicleAlarm alarm, VehicleLock lock, int insurance) {
        this.id = id;
        this.modelId = modelId;
        this.deaths = deaths;
        this.fuel = fuel;
        this.ownerId = ownerId;
        this.license = license;
        this.alarm = alarm;
        this.lock = lock;
        this.insurance = insurance;
    }

    public PlayerVehicleMetadata() {

    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
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

    public int getInsurance() {
        return insurance;
    }

    public void setInsurance(int insurance) {
        this.insurance = insurance;
    }
}
