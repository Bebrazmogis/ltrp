package lt.ltrp.job;

import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobVehicle;
import lt.ltrp.job.object.Rank;
import net.gtaun.shoebill.data.AngledLocation;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface JobController {

    List<Job> getJobs();

    Job getJob(int id);

    class Instance {
        static JobController instance;
    }

    static JobController get() {
        return Instance.instance;
    }


    JobVehicle createVehicle(int id, Job job, int modelId, AngledLocation location, int color1, int color2, Rank requiredRank, String license, float mileage);

    default JobVehicle createVehicle(Job job, int modelId, AngledLocation location, int color1, int color2, Rank requiredRank, float mileage) {
        return createVehicle(0, job, modelId, location, color1, color2, requiredRank, job.getName().substring(0, 3) + modelId, mileage);
    }

}
