package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.GovernmentFactionDao;
import lt.ltrp.job.dao.JobGateDao;
import lt.ltrp.job.dao.JobVehicleDao;
import lt.ltrp.object.GovernmentFaction;
import lt.ltrp.object.impl.GovernmentFactionImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class MySqlGovernmentFactionDaoImpl  implements GovernmentFactionDao {

    public MySqlGovernmentFactionDaoImpl(DataSource dataSource, EventManager eventManager) {

    }

}
