package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.job.dao.JobVehicleDao;
import lt.ltrp.dao.VehicleThiefDao;
import lt.ltrp.object.VehicleThiefJob;
import lt.ltrp.object.impl.VehicleThiefJobImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.23.
 */
public class MySqlVehicleThiefDaoImpl implements VehicleThiefDao {


    public MySqlVehicleThiefDaoImpl(DataSource dataSource, JobVehicleDao jobVehicleDao, EventManager eventManager) {

    }

}
