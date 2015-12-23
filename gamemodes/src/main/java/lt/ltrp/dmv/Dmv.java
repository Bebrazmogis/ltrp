package lt.ltrp.dmv;

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

    void startTest(LtrpPlayer player);

    int getStagePrice(int stage);
    void setStagePrice(int stage, int price);

    String getName();

    void addCheckpoint(DmvCheckpoint checkpoint);

    List<LtrpVehicle> getVehicles();
    void setVehicles(List<LtrpVehicle> vehicles);

    boolean isUserInTest(LtrpPlayer player);



}
