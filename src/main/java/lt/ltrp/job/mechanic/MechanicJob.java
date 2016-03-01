package lt.ltrp.job.mechanic;

import lt.ltrp.job.ContractJob;
import lt.ltrp.job.JobProperty;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class MechanicJob extends ContractJob {

    /**
     * The amount it costs to remove hydraulics from a vehicle
     */
    @JobProperty("remove_hydraulics_price")
    public int hydraulicsInstallPrice;

    @JobProperty("install_hydraulic_price")
    public int hydraulicRemovePrice;

    @JobProperty("wheel_price")
    public int wheelPrice;

    public MechanicJob(int id) {
        super(id);
    }

    public int getHydraulicsInstallPrice() {
        return hydraulicsInstallPrice;
    }

    public int getHydraulicRemovePrice() {
        return hydraulicRemovePrice;
    }

    public int getWheelPrice() {
        return wheelPrice;
    }
}
