package lt.ltrp;

import lt.ltrp.constant.PlayerVehiclePermission;
import lt.ltrp.data.PlayerVehicleArrest;
import lt.ltrp.data.PlayerVehicleMetadata;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.shopplugin.VehicleShopPlugin;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Destroyable;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.15.
 */
public interface PlayerVehicleController extends Destroyable {

    class Instance {
        static PlayerVehicleController instance;
    }

    static PlayerVehicleController get() {
        return Instance.instance;
    }




    Collection<PlayerVehiclePermission> getPermissions(int vehicleUId, LtrpPlayer player) ;
    PlayerVehicle loadVehicle(int uid);
    boolean isSpawned(int vehicleId);
    void setLicensePlate(PlayerVehicle vehicle);
    void destroyVehicle(PlayerVehicle vehicle);
    PlayerVehicleArrest getArrest(int vehicleId);
    PlayerVehicleArrest getArrest(String licensePlate);
    PlayerVehicleMetadata getMetaData(int vehicleId);
    int[] getArrestedVehicles(LtrpPlayer player);
    int getInsurancePrice(PlayerVehicle vehicle);
    int getParkingSpaceCost(Location location);
    int getScrapPrice(PlayerVehicle vehicle);
    Collection<PlayerVehicle> getSpawnedVehicles(LtrpPlayer player);
    int getPlayerOwnedVehicleCount(LtrpPlayer player);
    int getLicensePrice();
    int getMaxOwnedVehicles(LtrpPlayer player);
    int[] getVehicles(LtrpPlayer player);
    VehicleShopPlugin getShopPlugin();

}
