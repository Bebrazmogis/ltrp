package lt.ltrp.dao;

import lt.ltrp.object.MedicFaction;

/**
 * @author Bebras
 *         2016.05.24.
 */
public interface MedicFactionDao extends JobDao {

    MedicFaction get(int id);

}
