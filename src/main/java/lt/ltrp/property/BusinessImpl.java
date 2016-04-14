package lt.ltrp.property;

import lt.ltrp.property.object.Business;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class BusinessImpl extends PropertyImpl implements Business {


    private static List<Business> businesses = new ArrayList<>();

    public static Business create(int uniqueid, String name, EventManager eventManager1) {
        return create(uniqueid, name, null, null, eventManager1);
    }


    public static Business create(int uniqueid, String name, Location entrance, Location exit, EventManager eventM) {
        Business property = new BusinessImpl(uniqueid, name, eventM);
        property.setEntrance(entrance);
        property.setExit(exit);

        businesses.add(property);
        return property;

    }

    public static Business get(int id) {
        for(Business b : businesses) {
            if(b.getUid() == id) {
                return b;
            }
        }
        return null;
    }


    public BusinessImpl(int uniqueid, String name, EventManager eventManager1) {
        super(uniqueid, name, eventManager1);
    }

}
