package lt.ltrp.object;

import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface ContractJob extends Job {

    void setRanks(List<ContractJobRank> ranks);
    int getMaxPaycheck();
    void setMaxPaycheck(int maxPaycheck);
    int getMinPaycheck();
    void setMinPaycheck(int minPaycheck);
    int getContractLength();
    void setContractLength(int contractLength);

    @Override
    Collection<ContractJobRank> getRanks();

    @Override
    ContractJobRank getRank(int id);

    Collection<JobVehicle> getVehicles(ContractJobRank rank);
}
