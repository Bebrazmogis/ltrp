package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.dao.MechanicJobDao;
import lt.ltrp.object.MechanicJob;
import lt.ltrp.object.impl.MechanicJobImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlMechanicJobDaoImpl extends MySqlJobDaoImpl implements MechanicJobDao {


    public MySqlMechanicJobDaoImpl(DataSource dataSource, JobVehicleDao jobVehicleDao, EventManager eventManager) {
        super(dataSource, jobVehicleDao, eventManager);
    }

    @Override
    public MechanicJob get(int id) {
        MechanicJobImpl job = null;
        if(isValid(id)) {
            job = new MechanicJobImpl(id, getEventManager());
            try {
                load(job);
            } catch (LoadingException e) {
                e.printStackTrace();
            }
        }
        return job;
    }
}
