package lt.ltrp.dao;

import lt.ltrp.data.JailData;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.20.
 */
public interface JailDao {

    void insert(JailData data);
    JailData get(LtrpPlayer player);
    void update(JailData jailData);
    void remove(JailData data);

}
