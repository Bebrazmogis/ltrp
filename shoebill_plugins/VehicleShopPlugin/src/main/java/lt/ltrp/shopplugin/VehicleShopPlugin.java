package lt.ltrp.shopplugin;

import lt.ltrp.DatabasePlugin;
import lt.ltrp.shopplugin.dao.MySqlVehicleShopDao;
import lt.ltrp.shopplugin.dao.MySqlVehicleShopVehicleDao;
import lt.ltrp.shopplugin.dao.VehicleShopDao;
import lt.ltrp.shopplugin.dao.VehicleShopVehicleDao;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;


import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Bebras
 *         2016.03.15.
 */
public class VehicleShopPlugin extends Plugin {

    private static final String[] SQL_FILES = new String[] {
            "vehicle_shop.sql",
            "vehicle_shop_vehicles.sql"
    };
    private static Logger logger;

    private VehicleShopManager shopManager;


    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();

        DatabasePlugin db = Plugin.get(DatabasePlugin.class);
        parseTables(db.getDataSource());
        VehicleShopDao vehicleShopDao = new MySqlVehicleShopDao(db.getDataSource());
        VehicleShopVehicleDao vehicleShopVehicleDao = new MySqlVehicleShopVehicleDao(db.getDataSource());
        shopManager = new VehicleShopManager(getEventManager(), vehicleShopDao, vehicleShopVehicleDao);
    }


    @Override
    protected void onDisable() throws Throwable {
        shopManager.destroy();
        shopManager = null;
    }


    private void parseTables(DataSource ds) {
        Connection con = null;
        try {
            con = ds.getConnection();
            for(String name : SQL_FILES) {
                InputStream in = getClass().getClassLoader().getResourceAsStream(name);
                if(in == null) {
                    logger.error("Could not read resource " + name);
                    continue;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String query = "";
                String s;
                while ((s = reader.readLine()) != null) {
                    query += s;
                    if(s.endsWith(";")) {
                        logger.debug("query:" + query);
                        Statement stmt = con.createStatement();
                        stmt.execute(query);
                        stmt.close();
                        query = "";
                    }
                }
            }
        } catch(SQLException | IOException e) {
            logger.error("Error creating table " + e.getMessage());
        }
        finally {
            if(con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }


    /**
     *
     * @return Returns all existing vehicle shops
     */
    public VehicleShop[] getVehicleShops() {
        return shopManager.getVehicleShops();
    }

    /**
     * Finds the closest VehicleShop to a location in a specified distance
     * @param location the center location
     * @param maxDistance the maximum distance to search in
     * @return closest VehicleShop or null if there are none in range
     */
    public VehicleShop getClosestVehicleShop(Location location, float maxDistance) {
        return shopManager.getClosestShop(location, maxDistance);
    }

    /**
     * Finds the closest VehicleShop to a location
     * @param location the center location
     * @return closest VehicleShop or null if there are none in range
     */
    public VehicleShop getClosestVehicleShop(Location location) {
        return getClosestVehicleShop(location, Float.MAX_VALUE);
    }

    /**
     * Creates a new {@link VehicleShop}
     * @param location location of the VehicleShop
     * @param name name of the VehicleShop
     * @return thew newly created VehicleShop
     */
    public VehicleShop createVehicleShop(Location location, String name) {
        return shopManager.createShop(location, name);
    }

    /**
     * Updates an existing VehicleShop
     * @param shop vehicle shop to update
     */
    public void updateVehicleShop(VehicleShop shop) {
        shopManager.updateShop(shop);
    }

    /**
     * Destroys an existing vehicle shop
     * @param shop VehicleShop to destroy
     */
    public void removeVehicleShop(VehicleShop shop) {
        shopManager.removeShop(shop);
    }

    /**
     * Add a sold vehicle to a shop
     * @param shop shop that the vehicle will belong to
     * @param modelId vehicles model
     * @param price vehicle price
     */
    public void addShopVehicle(VehicleShop shop, int modelId, int price) {
        shopManager.insertShopVehicle(shop, modelId, price);
    }

    /**
     * Removes a vehicle from being sold in a {@link VehicleShop}
     * @param vehicle vehicle to remove
     */
    public void removeShopVehicle(ShopVehicle vehicle) {
        shopManager.removeShopVehicle(vehicle);
    }

    /**
     * Updates a vehicle
     * @param vehicle vehicle to be updated
     */
    public void updateShopVehicle(ShopVehicle vehicle) {
        shopManager.updateShopVehicle(vehicle);
    }

}
