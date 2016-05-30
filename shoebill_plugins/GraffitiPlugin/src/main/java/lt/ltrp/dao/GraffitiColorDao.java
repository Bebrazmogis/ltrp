package lt.ltrp.dao;

import lt.ltrp.data.GraffitiColor;

import java.util.List;

/**
 * @author Bebras
 *         2016.05.30.
 */
public interface GraffitiColorDao {

    GraffitiColor get(int uuid);
    List<GraffitiColor> get();
    void update(GraffitiColor color);
    void remove(GraffitiColor color);
    int insert(GraffitiColor color);

}
