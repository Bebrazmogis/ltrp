package lt.ltrp;

import lt.ltrp.dao.PlayerDao;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Destroyable;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.08.
 */
public interface PlayerController extends Destroyable {

    static final int MINUTES_FOR_PAYDAY = 20;
    public static final Location GYM_LOCATION = new Location();

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
    String getUsernameByUUID(int uuid);


}
