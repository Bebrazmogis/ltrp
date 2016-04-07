package lt.ltrp.dao;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerAddiction;
import lt.ltrp.player.PlayerDrugs;

/**
 * @author Bebras
 *         2016.04.05.
 */
public interface DrugAddictionDao {


    void insert(PlayerAddiction addiction);
    void update(PlayerAddiction addiction);
    void remove(PlayerAddiction addiction);
    PlayerDrugs get(LtrpPlayer player);

    void update(PlayerDrugs drugs);

}
