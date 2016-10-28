package lt.ltrp.aircraft;


import lt.ltrp.data.dmv.DmvCheckpoint;
import lt.ltrp.object.*;
import lt.ltrp.player.licenses.constant.LicenseType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.02.14.
 */
public class AircraftDmvImpl implements AircraftDmv {

    private static final ArrayList<LicenseType> LICENSE_TYPES = new ArrayList<>(1);

    static {
        LICENSE_TYPES.add(LicenseType.Aircraft);
    }

    private DmvCheckpoint[] checkpoints;
    private int id, checkpointTestPrice;
    private String name;
    private Location location;
    private List<DmvVehicle> vehicles;

    public AircraftDmvImpl(int id) {
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
        return FlyingTest.create(player, vehicle, this, eventManager);
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
