package lt.ltrp.shopplugin.dao;

import lt.ltrp.shopplugin.ShopVehicle;
import lt.ltrp.shopplugin.VehicleShop;

/**
 * @author Bebras
 *         2016.03.15.
 */
public interface VehicleShopVehicleDao {

    ShopVehicle[] get(VehicleShop vehicleShop);
    void insert(ShopVehicle vehicle);
    void remove(ShopVehicle vehicle);
    void update(ShopVehicle vehicle);

}
