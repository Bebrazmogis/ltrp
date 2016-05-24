package lt.ltrp.dao;

import lt.ltrp.object.PoliceFaction;

/**
 * @author Bebras
 *         2016.05.24.
 */
public interface PoliceFactionDao extends FactionDao {

    PoliceFaction get(int id);

}
