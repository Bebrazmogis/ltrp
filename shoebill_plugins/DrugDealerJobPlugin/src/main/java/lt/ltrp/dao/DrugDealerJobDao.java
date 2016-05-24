package lt.ltrp.dao;

import lt.ltrp.object.DrugDealerJob;

/**
 * @author Bebras
 *         2016.05.24.
 */
public interface DrugDealerJobDao extends JobDao {

    DrugDealerJob get(int id);

}
