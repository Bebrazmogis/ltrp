package lt.ltrp.object.impl;


import lt.ltrp.object.MedicFaction;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class MedicFactionImpl extends AbstractFaction implements MedicFaction {

    public MedicFactionImpl(int uuid, EventManager eventManager) {
        super(uuid, eventManager);
    }

}
