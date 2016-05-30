package lt.ltrp.dao;

import lt.ltrp.data.GraffitiFont;

import java.util.List;

/**
 * @author Bebras
 *         2016.05.30.
 */
public interface GraffitiFontDao {

    List<GraffitiFont> get();
    GraffitiFont get(int uuid);
    void update(GraffitiFont font);
    void remove(GraffitiFont font);
    int insert(GraffitiFont font);

}
