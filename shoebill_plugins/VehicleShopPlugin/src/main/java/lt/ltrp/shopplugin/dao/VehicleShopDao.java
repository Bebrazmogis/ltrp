package lt.ltrp.shopplugin.dao;

import lt.ltrp.shopplugin.VehicleShop;

/**
 * @author Bebras
 *         2016.03.15.
 */
public interface VehicleShopDao {

    VehicleShop[] get();
    void insert(VehicleShop shop);
    void update(VehicleShop shop);
    void remove(VehicleShop shop);

}
