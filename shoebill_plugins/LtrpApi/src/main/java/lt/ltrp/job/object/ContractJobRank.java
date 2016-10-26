package lt.ltrp.job.object;

import org.jetbrains.annotations.NotNull;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface ContractJobRank extends JobRank {

    int getXpNeeded();
    void setXpNeeded(int xpNeeded);
    void setJob(ContractJob job);

    @NotNull
    ContractJob getJob();

}
