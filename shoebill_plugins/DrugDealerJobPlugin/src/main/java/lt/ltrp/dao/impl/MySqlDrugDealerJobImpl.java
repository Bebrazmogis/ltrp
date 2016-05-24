package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.DrugDealerJobDao;
import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.object.DrugDealerJob;
import lt.ltrp.object.DrugDealerJobImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlDrugDealerJobImpl extends MySqlJobDaoImpl implements DrugDealerJobDao {


    public MySqlDrugDealerJobImpl(DataSource dataSource, JobVehicleDao jobVehicleDao, EventManager eventManager) {
        super(dataSource, jobVehicleDao, eventManager);
    }

    @Override
    public DrugDealerJob get(int id) {
        DrugDealerJobImpl impl = null;
        if(isValid(id)) {
            impl = new DrugDealerJobImpl(id, getEventManager());
            try {
                load(impl);
            } catch (LoadingException e) {
                e.printStackTrace();
            }
        }
        return impl;
    }
}
