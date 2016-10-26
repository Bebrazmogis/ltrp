package lt.ltrp.job.object;

import org.jetbrains.annotations.NotNull;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface FactionRank extends JobRank {

    void setNumber(int number);
    void setName(String name);
    void setJob(Faction faction);
    @NotNull
    Faction getJob();

}
