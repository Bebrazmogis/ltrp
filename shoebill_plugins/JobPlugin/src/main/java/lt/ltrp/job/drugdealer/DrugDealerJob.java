package lt.ltrp.job.drugdealer;

import lt.ltrp.job.ContractJob;
import lt.ltrp.job.Job;
import lt.ltrp.job.JobProperty;

/**
 * @author Bebras
 *         2016.02.06.
 */
public class DrugDealerJob extends ContractJob {

    @JobProperty("seed_price")
    public int seedPrice;

    public DrugDealerJob(int id) {
        super(id);
    }

    public int getSeedPrice() {
        return seedPrice;
    }
}
