package lt.ltrp;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface PropertyController {


    Collection<Property> getProperties();
    Property get(LtrpPlayer player);

    class Instance {
        static PropertyController instance;
    }
    
    static PropertyController get() {
        return Instance.instance;
    }
    
    
    
}

