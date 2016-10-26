package lt.ltrp.job.dao;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.VehicleDao;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobVehicle;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.23.
 */
public interface JobVehicleDao extends VehicleDao {

    Collection<JobVehicle> get(Job job) throws LoadingException;
    int insert(JobVehicle vehicle);
    void delete(JobVehicle vehicle);
    void update(JobVehicle vehicle);

}
