package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.DrugDealerJobDao;
import lt.ltrp.job.dao.JobVehicleDao;
import lt.ltrp.object.DrugDealerJob;
import lt.ltrp.object.impl.DrugDealerJobImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlDrugDealerJobImpl implements DrugDealerJobDao {


    public MySqlDrugDealerJobImpl(DataSource dataSource, EventManager eventManager) {

    }
}
