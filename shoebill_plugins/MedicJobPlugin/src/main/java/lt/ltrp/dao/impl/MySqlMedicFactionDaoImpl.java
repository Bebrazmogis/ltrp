package lt.ltrp.dao.impl;

import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.dao.MedicFactionDao;
import lt.ltrp.object.MedicFaction;
import lt.ltrp.object.impl.MedicFactionImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlMedicFactionDaoImpl extends MySqlFactionDaoImpl implements MedicFactionDao {

    public MySqlMedicFactionDaoImpl(DataSource dataSource, JobVehicleDao vehicleDao, EventManager eventManager) {
        super(dataSource, vehicleDao, eventManager);
    }

    @Override
    public MedicFaction get(int i) {
        MedicFaction faction = null;
        if(isValid(i)) {
            faction = new MedicFactionImpl(i, getEventManager());

        }
        return faction;
    }
}
