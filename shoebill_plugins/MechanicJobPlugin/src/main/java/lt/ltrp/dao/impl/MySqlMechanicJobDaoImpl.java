package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.job.dao.JobVehicleDao;
import lt.ltrp.dao.MechanicJobDao;
import lt.ltrp.object.MechanicJob;
import lt.ltrp.object.impl.MechanicJobImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlMechanicJobDaoImpl implements MechanicJobDao {


    public MySqlMechanicJobDaoImpl(DataSource dataSource, JobVehicleDao jobVehicleDao, EventManager eventManager) {

    }
}
