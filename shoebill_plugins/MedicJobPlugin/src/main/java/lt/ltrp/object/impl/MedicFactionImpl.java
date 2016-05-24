package lt.ltrp.object.impl;


import lt.ltrp.AbstractFaction;
import lt.ltrp.object.MedicFaction;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class MedicFactionImpl extends AbstractFaction implements MedicFaction {

    public MedicFactionImpl(int id, String name, Location location, int basePaycheck, EventManager eventManager) {
        super(id, name, location, basePaycheck, eventManager);
    }

    public MedicFactionImpl(int id, EventManager eventManager) {
        this(id, null, null, 0, eventManager);
    }
}
