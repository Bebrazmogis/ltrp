package lt.ltrp.object;

import lt.ltrp.data.TrashMissions;
import lt.ltrp.job.object.ContractJob;
import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface TrashManJob extends ContractJob {

    int getTrashMasterCapacity();
    int getTrashRouteBonus();
    int getTrashPickupBonus();
    TrashMissions getMissions();
    void setMissions(TrashMissions missions);
    Location getDropOffLocation();
}
