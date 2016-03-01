package lt.ltrp.job.mechanic;

import lt.ltrp.job.ContractJob;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class MechanicJob extends ContractJob {

    public MechanicJob(ContractJob job) {
        super();
        super.setId(job.getId());
        super.setLocation(job.getLocation());
        super.setName(job.getName());
        super.setContractLength(job.getContractLength());
        super.setMinPaycheck(job.getMinPaycheck());
        super.setMaxPaycheck(job.getMaxPaycheck());
        super.setRanks(job.getRanks());
    }

}
