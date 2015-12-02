package lt.ltrp.property;

import net.gtaun.shoebill.data.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class Garage extends Property {

    private static List<Garage> garageList = new ArrayList<>();

    public static Garage create(int uniqueid, String name) {
        return create(uniqueid, name, null, null);
    }

    public static Garage create(int uniqueid, String name, Location entrance, Location exit) {
        Garage property = new Garage(uniqueid, name);
        property.setEntrance(entrance);
        property.setExit(exit);

        garageList.add(property);
        return property;

    }

    public static Garage get(int id) {
        for(Garage g : garageList) {
            if(g.getUid() == id) {
                return g;
            }
        }
        return null;
    }


    public Garage(int uniqueid, String name) {
        super(uniqueid, name);
    }
}
