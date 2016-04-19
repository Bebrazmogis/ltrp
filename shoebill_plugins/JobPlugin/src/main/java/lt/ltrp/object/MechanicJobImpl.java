package lt.ltrp.object;

import lt.ltrp.AbstractContractJob;
import lt.ltrp.JobProperty;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class MechanicJobImpl extends AbstractContractJob implements MechanicJob {

    /**
     * The amount it costs to remove hydraulics from a vehicle
     */
    @JobProperty("remove_hydraulics_price")
    public int hydraulicsInstallPrice;

    @JobProperty("install_hydraulic_price")
    public int hydraulicRemovePrice;

    @JobProperty("wheel_price")
    public int wheelPrice;

    public MechanicJobImpl(int id, String name, Location location, int basePaycheck, EventManager eventManager, int contractLength, int maxPaycheck, int minPaycheck, int hydraulicsInstallPrice, int hydraulicRemovePrice, int wheelPrice) {
        super(id, name, location, basePaycheck, eventManager, contractLength, maxPaycheck, minPaycheck);
        this.hydraulicsInstallPrice = hydraulicsInstallPrice;
        this.hydraulicRemovePrice = hydraulicRemovePrice;
        this.wheelPrice = wheelPrice;
        Instance.instance = this;
    }

    public MechanicJobImpl(int id, EventManager eventManager) {
        this(id, null, null, 0, eventManager, 0, 0, 0, 0, 0, 0);
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
