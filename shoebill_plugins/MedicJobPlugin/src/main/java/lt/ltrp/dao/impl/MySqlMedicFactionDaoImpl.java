package lt.ltrp.business.dao.impl;

import lt.ltrp.job.dao.JobGateDao;
import lt.ltrp.job.dao.JobVehicleDao;
import lt.ltrp.dao.MedicFactionDao;
import lt.ltrp.object.MedicFaction;
import lt.ltrp.object.impl.MedicFactionImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlMedicFactionDaoImpl implements MedicFactionDao {

    private DataSource dataSource;
    private EventManager eventManager;

    public MySqlMedicFactionDaoImpl(DataSource dataSource, EventManager eventManager) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
    }
}
