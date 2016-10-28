package lt.ltrp.object.impl;

import lt.ltrp.object.GovernmentFaction;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class GovernmentFactionImpl extends AbstractFaction implements GovernmentFaction {


    public GovernmentFactionImpl(int uuid, EventManager eventManager) {
        super(uuid, eventManager);
    }


}
