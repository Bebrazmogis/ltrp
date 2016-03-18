package lt.ltrp.job.trashman;

import lt.ltrp.job.ContractJob;
import lt.ltrp.job.JobProperty;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class TrashManJob extends ContractJob {


    @JobProperty("trashmaster_capacity")
    public int trashMasterCapacity;

    @JobProperty("trash_route_bonus")
    public int trashRouteBonus;



    public TrashManJob(int id) {
        super(id);
    }

    public int getTrashMasterCapacity() {
        return trashMasterCapacity;
    }

    public int getTrashRouteBonus() {
        return trashRouteBonus;
    }
}
