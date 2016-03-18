package lt.ltrp.dmv.boat;

import lt.ltrp.constant.LicenseType;
import lt.ltrp.dmv.*;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class BoatDmv implements CheckpointDmv {

    private static final ArrayList<LicenseType> LICENSE_TYPES = new ArrayList<>(1);

    static {
        LICENSE_TYPES.add(LicenseType.Ship);
    }

    private DmvCheckpoint[] checkpoints;
    private int id, checkpointTestPrice;
    private String name;
    private Location location;
    private List<DmvVehicle> vehicles;


    public BoatDmv(int id) {
        this.id = id;
    }

    @Override
    public DmvCheckpoint[] getCheckpoints() {
        return checkpoints;
    }

    @Override
    public void setCheckpoints(DmvCheckpoint[] checkpoints) {
        this.checkpoints = checkpoints;
    }

    @Override
    public AbstractCheckpointTest startCheckpointTest(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager) {
        return BoatingTest.create(player, vehicle, this, eventManager);
    }

    @Override
    public int getCheckpointTestPrice() {
        return checkpointTestPrice;
    }

    public void setCheckpointTestPrice(int checkpointTestPrice) {
        this.checkpointTestPrice = checkpointTestPrice;
    }

    @Override
    public void setLocation(Location loc) {
        this.location = loc;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<DmvVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(List<DmvVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public List<LicenseType> getLicenseType() {
        return LICENSE_TYPES;
    }
}
