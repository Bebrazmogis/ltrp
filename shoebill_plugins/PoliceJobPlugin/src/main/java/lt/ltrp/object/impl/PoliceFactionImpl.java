package lt.ltrp.object.impl;

import lt.ltrp.constant.JobProperty;
import lt.ltrp.object.PoliceFaction;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class PoliceFactionImpl extends AbstractFaction implements PoliceFaction {

    @JobProperty("is_taser_enabled")
    private int taserEnabled;

    public PoliceFactionImpl(int id, EventManager eventManager) {
        super(id, eventManager);
    }

    @Override
    public boolean isTaserEnabled() {
        return taserEnabled != 0;
    }

    @Override
    public void setTaserEnabled(boolean set) {
        taserEnabled = set ? 1 : 0;
    }
}
