package lt.ltrp;

import lt.ltrp.dao.JobDao;
import lt.ltrp.data.JobData;
import lt.ltrp.object.*;
import net.gtaun.shoebill.data.AngledLocation;

import java.lang.String;import java.util.List;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface JobController {

    List<Job> getJobs();
    List<ContractJob> getContractJobs();
    List<Faction> getFactions();

    Job getJob(int id);
    Job getJob(LtrpPlayer player);
    Faction getFaction(LtrpPlayer player);
    ContractJob getContractJob(LtrpPlayer player);

    JobData getJobData(LtrpPlayer player);

    void setJob(LtrpPlayer player, Job job);
    void setJob(LtrpPlayer player, Job job, Rank rank);
    void setRank(LtrpPlayer player, Rank rank);


    class Instance {
        static JobController instance;
    }

    static JobController get() {
        return Instance.instance;
    }


    JobVehicle createVehicle(int id, Job job, int modelId, AngledLocation location, int color1, int color2, Rank requiredRank, String license, float mileage);
    JobDao getDao();
    default boolean isLeader(LtrpPlayer player) {
        return isLeader(player.getUUID());
    }
    boolean isLeader(int userId);


}
