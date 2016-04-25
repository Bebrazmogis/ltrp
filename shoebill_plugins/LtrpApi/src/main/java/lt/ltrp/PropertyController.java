package lt.ltrp;

import lt.ltrp.constant.BusinessType;
import lt.ltrp.dao.BusinessDao;
import lt.ltrp.dao.GarageDao;
import lt.ltrp.dao.HouseDao;
import lt.ltrp.data.BusinessCommodity;
import lt.ltrp.object.Business;
import lt.ltrp.object.Garage;
import lt.ltrp.object.House;
import lt.ltrp.object.Property;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.lang.String;import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface PropertyController {



    House createHouse(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit,
                      lt.ltrp.data.Color labelColor, int money, int rentprice, EventManager eventManager);

    Business createBusiness(int id, String name, BusinessType type, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor,
                            int money, int resources, int commodityLimit, EventManager eventManager);

    Garage createGarage(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, AngledLocation vehicleEntrance, AngledLocation vehicleExit,
                        Color labelColor, EventManager eventManager);

    Collection<Garage> getGarages();

    Collection<Business> getBusinesses();

    Collection<House> getHouses();

    Collection<Property> getProperties();

    List<BusinessCommodity> getAvailableCommodities(BusinessType type);

    HouseDao getHouseDao();
    BusinessDao getBusinessDao();
    GarageDao getGarageDao();

    class Instance {
        static PropertyController instance;
    }
    
    static PropertyController get() {
        return Instance.instance;
    }
    
    
    
}

