package lt.ltrp.property;

import net.gtaun.shoebill.data.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class Business extends Property {


    private static List<Business> businesses = new ArrayList<>();

    public static Business create(int uniqueid, String name) {
        return create(uniqueid, name, null, null);
    }

    public static Business create(int uniqueid, String name, Location entrance, Location exit) {
        Business property = new Business(uniqueid, name);
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


    public Business(int uniqueid, String name) {
        super(uniqueid, name);
    }

}
