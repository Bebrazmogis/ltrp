package lt.ltrp.dao;

import lt.ltrp.object.Job;
import lt.ltrp.object.JobGate;

import java.util.List;

/**
 * @author Bebras
 *         2016.05.31.
 */
public interface JobGateDao {

    List<JobGate> get(Job job);
    List<JobGate> get();
    JobGate get(int uuid);
    void update(JobGate gate);
    void remove(JobGate gate);
    int insert(JobGate gate);

}
