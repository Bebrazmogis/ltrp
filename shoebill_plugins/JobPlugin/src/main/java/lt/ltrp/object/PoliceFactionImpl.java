package lt.ltrp.object;

import lt.ltrp.AbstractFaction;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class PoliceFactionImpl extends AbstractFaction implements PoliceFaction {

    public PoliceFactionImpl(int id, String name, Location location, int basePaycheck, EventManager eventManager) {
        super(id, name, location, basePaycheck, eventManager);
        Instance.instance = this;
    }

    public PoliceFactionImpl(int id, EventManager eventManager) {
        this(id, null, null , 0, eventManager);
    }
}
