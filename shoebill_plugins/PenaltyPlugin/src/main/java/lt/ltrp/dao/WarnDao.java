package lt.ltrp.dao;

import lt.ltrp.data.WarnData;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.20.
 */
public interface WarnDao {

    Collection<WarnData> get(int userId);
    int getCount(int userId);
    void remove(WarnData warnData);
    void insert(WarnData warnData);
    void update(WarnData warnData);

}
