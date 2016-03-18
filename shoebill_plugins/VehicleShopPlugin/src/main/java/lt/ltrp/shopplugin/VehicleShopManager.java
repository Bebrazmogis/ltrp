package lt.ltrp.shopplugin;

import lt.ltrp.shopplugin.dao.VehicleShopDao;
import lt.ltrp.shopplugin.dao.VehicleShopVehicleDao;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

/**
 * @author Bebras
 *         2016.03.15.
 */
public class VehicleShopManager implements Destroyable {

    private EventManagerNode eventManagerNode;
    private VehicleShop[] vehicleShops;
    private boolean destroyed;
    private VehicleShopDao vehicleShopDao;
    private VehicleShopVehicleDao vehicleShopVehicleDao;

    public VehicleShopManager(EventManager eventManager, VehicleShopDao vehicleShopDao, VehicleShopVehicleDao shopVehicleDao) {
        this.eventManagerNode = eventManager.createChildNode();
        this.vehicleShopDao = vehicleShopDao;
        this.vehicleShopVehicleDao = shopVehicleDao;

        this.vehicleShops = vehicleShopDao.get();
        for(VehicleShop shop : vehicleShops) {
            shop.setVehicles(shopVehicleDao.get(shop));
        }
    }

    @Override
    public void destroy() {
        this.destroyed = true;
        this.eventManagerNode.cancelAll();
        for(int i = 0; i < vehicleShops.length; i++) {
            vehicleShops[i].destroy();
        }
        vehicleShops = null;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    protected VehicleShop[] getVehicleShops() {
        return vehicleShops;
    }

    protected VehicleShop getClosestShop(Location location, float maxDistance) {
        float closestSoFar = maxDistance;
        VehicleShop closest = null;
        for(int i = 0; i < vehicleShops.length; i++) {
            float distance = vehicleShops[i].getLocation().distance(location);
            if(distance < closestSoFar) {
                closest = vehicleShops[i];
                closestSoFar = distance;
            }
        }
        return closest;
    }

    protected VehicleShop createShop(Location location, String name) {
        VehicleShop shop = new VehicleShop(location, name);
        this.vehicleShopDao.insert(shop);
        VehicleShop[] tmpShops = new VehicleShop[vehicleShops.length+1];
        tmpShops[0] = shop;
        for(int i = 0; i < vehicleShops.length; i++) {
            tmpShops[i+1] = vehicleShops[i];
        }
        vehicleShops = tmpShops;
        return shop;
    }

    protected void updateShop(VehicleShop shop) {
        this.vehicleShopDao.update(shop);
    }

    protected void removeShop(VehicleShop shop) {
        this.vehicleShopDao.remove(shop);

        // Create a smaller array to hold all the shops except the one we delete
        VehicleShop[] tmpShops = new VehicleShop[vehicleShops.length - 1];
        int count = 0;
        for(int i = 0; i < vehicleShops.length; i++) {
            if(!vehicleShops[i].equals(shop)) {
                tmpShops[count++] = vehicleShops[i];
            } else {
                vehicleShops[i].destroy();
            }
        }
        vehicleShops = tmpShops;
    }

    protected void insertShopVehicle(VehicleShop shop, int modelId, int price) {
        ShopVehicle vehicle = new ShopVehicle(modelId, price, shop);
        shop.addVehicle(vehicle);
        vehicleShopVehicleDao.insert(vehicle);
    }

    protected void removeShopVehicle(ShopVehicle vehicle) {
        vehicle.getShop().removeVehicle(vehicle);
        vehicleShopVehicleDao.remove(vehicle);
    }

    protected void updateShopVehicle(ShopVehicle vehicle) {
        vehicleShopVehicleDao.update(vehicle);
    }
}
