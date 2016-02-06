package lt.ltrp.dmv;

import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.Location;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.25.
 */
public class DmvImpl implements Dmv {

    private int id;
    private String name;
    private Location location;
    private List<LtrpVehicle> vehicles;

    protected DmvImpl(Dmv dmv) {
        this.id = dmv.getId();
        this.name = dmv.getName();
        this.location = dmv.getLocation();
        this.vehicles = dmv.getVehicles();
    }

    public DmvImpl(int id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
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
    public List<LtrpVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(List<LtrpVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
