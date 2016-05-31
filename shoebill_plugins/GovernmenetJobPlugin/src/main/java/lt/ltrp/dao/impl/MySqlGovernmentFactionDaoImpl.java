package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.GovernmentFactionDao;
import lt.ltrp.dao.JobGateDao;
import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.object.GovernmentFaction;
import lt.ltrp.object.impl.GovernmentFactionImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class MySqlGovernmentFactionDaoImpl extends MySqlFactionDaoImpl implements GovernmentFactionDao {

    public MySqlGovernmentFactionDaoImpl(DataSource dataSource, JobVehicleDao vehicleDao, JobGateDao jobGateDao, EventManager eventManager) {
        super(dataSource, vehicleDao, jobGateDao, eventManager);
    }

    @Override
    public GovernmentFaction get(int i) {
        GovernmentFaction faction = new GovernmentFactionImpl(getEventManager());
        try {
            load(faction);
        } catch (LoadingException e) {
            e.printStackTrace();
        }
        return faction;
    }
}
