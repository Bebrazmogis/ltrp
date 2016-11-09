package lt.ltrp.object.impl;

import lt.ltrp.constant.JobProperty;
import lt.ltrp.object.DrugDealerJob;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.06.
 */
public class DrugDealerJobImpl extends AbstractContractJob implements DrugDealerJob {

    @JobProperty("seed_price")
    public int seedPrice;

    public DrugDealerJobImpl(int id, EventManager eventManager) {
        super(id, eventManager);
    }


    public int getSeedPrice() {
        return seedPrice;
    }
}
