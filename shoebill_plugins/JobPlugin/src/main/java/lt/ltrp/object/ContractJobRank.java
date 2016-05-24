package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface ContractJobRank extends Rank {

    int getXpNeeded();
    void setXpNeeded(int xpNeeded);
    void setJob(ContractJob job);
    ContractJob getJob();

}
