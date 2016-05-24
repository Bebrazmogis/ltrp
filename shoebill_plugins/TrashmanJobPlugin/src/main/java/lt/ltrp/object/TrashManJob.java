package lt.ltrp.object;

import lt.ltrp.data.TrashMissions;
import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface TrashManJob extends ContractJob {

    class Instance {
        static TrashManJob instance;
    }

    static TrashManJob get() {
        return Instance.instance;
    }

    int getTrashMasterCapacity();
    int getTrashRouteBonus();
    int getTrashPickupBonus();
    TrashMissions getMissions();
    void setMissions(TrashMissions missions);
    Location getDropOffLocation();
}
