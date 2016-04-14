package lt.ltrp.job;

import lt.ltrp.job.object.Job;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface JobManager {

    List<Job> getJobs();

    Job getJob(int id);

    class Instance {
        static JobManager instance;
    }

    static JobManager get() {
        return Instance.instance;
    }



}
