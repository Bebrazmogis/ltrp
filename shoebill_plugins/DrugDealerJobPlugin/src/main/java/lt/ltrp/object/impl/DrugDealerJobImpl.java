package lt.ltrp.object.impl;

import lt.ltrp.AbstractContractJob;
import lt.ltrp.JobProperty;
import lt.ltrp.object.DrugDealerJob;import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.06.
 */
public class DrugDealerJobImpl extends AbstractContractJob implements DrugDealerJob {

    @JobProperty("seed_price")
    public int seedPrice;

    public DrugDealerJobImpl(int id, EventManager eventManager) {
        this(id, null, null, 0, eventManager, 0, 0, 0, 0);
    }

    public DrugDealerJobImpl(int id, String name, Location location, int basePaycheck, EventManager eventManager, int contractLength, int maxPaycheck, int minPaycheck, int seedPrice) {
        super(id, name, location, basePaycheck, eventManager, contractLength, maxPaycheck, minPaycheck);
        this.seedPrice = seedPrice;
    }


    public int getSeedPrice() {
        return seedPrice;
    }
}
