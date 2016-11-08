package lt.ltrp.job.dao;

import lt.ltrp.dao.DaoException;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobGate;

import java.util.List;

/**
 * @author Bebras
 *         2016.05.31.
 */
public interface JobGateDao {

    List<JobGate> get(Job job) throws DaoException;
    void update(JobGate gate) throws DaoException;
    void remove(JobGate gate) throws DaoException;
    int insert(JobGate gate) throws DaoException;

}
