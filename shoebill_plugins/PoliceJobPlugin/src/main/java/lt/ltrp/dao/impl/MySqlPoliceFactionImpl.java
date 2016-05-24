package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.dao.PoliceFactionDao;
import lt.ltrp.object.PoliceFaction;
import lt.ltrp.object.impl.PoliceFactionImpl;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MySqlPoliceFactionImpl extends MySqlFactionDaoImpl implements PoliceFactionDao {

    public MySqlPoliceFactionImpl(DataSource dataSource, JobVehicleDao vehicleDao, EventManager eventManager) {
        super(dataSource, vehicleDao, eventManager);
    }

    @Override
    public PoliceFaction get(int i) {
        PoliceFactionImpl impl = null;
        if(isValid(i)) {
            impl = new PoliceFactionImpl(i, getEventManager());
            try {
                load(impl);
            } catch (LoadingException e) {
                e.printStackTrace();
            }
        }
        return impl;
    }
}
