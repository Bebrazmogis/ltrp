package lt.ltrp.dao;

import lt.ltrp.object.MechanicJob;

/**
 * @author Bebras
 *         2016.05.24.
 */
public interface MechanicJobDao extends JobDao {

    MechanicJob get(int id);


}
