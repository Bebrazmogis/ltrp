package lt.ltrp.job.dao;


import lt.ltrp.dao.DaoException;
import lt.ltrp.job.object.Job;
import lt.ltrp.player.job.data.PlayerJobData;

import java.util.Collection;

/**
 * @author Bebras
 *         2015.12.06.
 */
public interface JobDao {

    boolean isValid(int jobId) throws DaoException;
    default boolean isValid(Job job) throws DaoException {
        return isValid(job.getUUID());
    }
    void update(Job job) throws DaoException;
    int insert(Job job) throws DaoException;

    void parseProperties(Job job) throws DaoException;

    Collection<PlayerJobData> getEmployees(Job job);
}
