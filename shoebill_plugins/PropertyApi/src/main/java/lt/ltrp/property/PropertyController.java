package lt.ltrp.property;

import lt.ltrp.property.object.*;
import lt.ltrp.property.object.Business;
import lt.ltrp.property.object.Garage;
import lt.ltrp.property.object.House;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface PropertyController {



    House createHouse(int uniqueId, String name, Location entrance, Location exit, EventManager eventManager);

    Business createBusiness(int uniqueId, String name, Location entrance, Location exit, EventManager eventManager);

    Garage createGarage(int uniqueId, String name, Location entrance, Location exit, EventManager eventManager);

    Collection<Garage> getGarages();

    Collection<Business> getBusinesses();

    Collection<House> getHouses();

    Collection<lt.ltrp.property.object.Property> getProperties();

    class Instance {
        static PropertyController instance;
    }
    
    static PropertyController get() {
        return Instance.instance;
    }
    
    
    
}

