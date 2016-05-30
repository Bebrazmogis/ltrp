package lt.ltrp.dao;

import lt.ltrp.object.Graffiti;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.30.
 */
public interface GraffitiDao {

    Collection<Graffiti> get();
    Graffiti get(int uuid);
    void update(Graffiti graffiti);
    void remove(Graffiti graffiti);
    int insert(Graffiti graffiti);

}
