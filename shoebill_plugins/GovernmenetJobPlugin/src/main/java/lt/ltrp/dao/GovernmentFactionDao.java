package lt.ltrp.dao;

import lt.ltrp.object.GovernmentFaction;

/**
 * @author Bebras
 *         2016.05.31.
 */
public interface GovernmentFactionDao  extends FactionDao {

    @Override
    GovernmentFaction get(int i);
}
