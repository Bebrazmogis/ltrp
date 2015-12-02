package lt.ltrp.property;

import net.gtaun.shoebill.data.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class House extends Property {

    private static List<House> houseList = new ArrayList<>();

    public static House create(int uniqueid, String name) {
        return create(uniqueid, name, null, null);
    }

    public static House create(int uniqueid, String name, Location entrance, Location exit) {
        House property = new House(uniqueid, name);
        property.setEntrance(entrance);
        property.setExit(exit);

        houseList.add(property);
        return property;

    }

    public static House get(int id) {
        for(House h : houseList) {
            if(h.getUid() == id) {
                return h;
            }
        }
        return null;
    }


    public House(int uniqueid, String name) {
        super(uniqueid, name);
    }


}
