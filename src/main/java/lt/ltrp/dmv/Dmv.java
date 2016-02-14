package lt.ltrp.dmv;

import lt.ltrp.constant.LicenseType;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.Location;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.13.
 */
public interface Dmv {

    void setLocation(Location loc);
    Location getLocation();

    int getId();
    void setId(int id);

    String getName();
    void setName(String name);

    List<DmvVehicle> getVehicles();
    void setVehicles(List<DmvVehicle> vehicles);

    List<LicenseType> getLicenseType();




}
