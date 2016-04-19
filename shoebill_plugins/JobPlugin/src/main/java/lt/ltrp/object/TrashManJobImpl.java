package lt.ltrp.object;

import lt.ltrp.AbstractContractJob;
import lt.ltrp.JobProperty;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class TrashManJobImpl extends AbstractContractJob implements TrashManJob {


    @JobProperty("trashmaster_capacity")
    public int trashMasterCapacity;

    @JobProperty("trash_route_bonus")
    public int trashRouteBonus;

    public TrashManJobImpl(int id, String name, Location location, int basePaycheck, EventManager eventManager, int contractLength, int maxPaycheck, int minPaycheck, int trashMasterCapacity, int trashRouteBonus) {
        super(id, name, location, basePaycheck, eventManager, contractLength, maxPaycheck, minPaycheck);
        this.trashMasterCapacity = trashMasterCapacity;
        this.trashRouteBonus = trashRouteBonus;
        Instance.instance = this;
    }

    public TrashManJobImpl(int id, EventManager eventManager) {
        this(id, null, null, 0, eventManager, 0, 0, 0, 0, 0);
    }

    public int getTrashMasterCapacity() {
        return trashMasterCapacity;
    }

    public int getTrashRouteBonus() {
        return trashRouteBonus;
    }
}
