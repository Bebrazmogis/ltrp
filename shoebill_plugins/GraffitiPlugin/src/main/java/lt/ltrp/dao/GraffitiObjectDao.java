package lt.ltrp.dao;

import lt.ltrp.data.GraffitiObject;

import java.util.List;

/**
 * @author Bebras
 *         2016.05.30.
 */
public interface GraffitiObjectDao {

    List<GraffitiObject> get();
    GraffitiObject get(int uuid);
    void update(GraffitiObject object);
    void remove(GraffitiObject object);
    int insert(GraffitiObject object);

}
