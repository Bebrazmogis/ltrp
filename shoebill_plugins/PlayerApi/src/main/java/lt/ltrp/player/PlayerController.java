package lt.ltrp.player;

import lt.ltrp.player.dao.PlayerDao;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.object.Destroyable;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.08.
 */
public interface PlayerController extends Destroyable {

    static final int MINUTES_FOR_PAYDAY = 20;

    class Instance
    {
        static PlayerController instance = null;
    }

    static PlayerController get()
    {
        return Instance.instance;
    }

    Collection<LtrpPlayer> getPlayers();
    PlayerDao getPlayerDao();


}
