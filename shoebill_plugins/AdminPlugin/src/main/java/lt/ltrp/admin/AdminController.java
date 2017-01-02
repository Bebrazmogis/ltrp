package lt.ltrp;

import lt.ltrp.player.object.LtrpPlayer;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface AdminController {


    class Instance {
        static AdminController instance;
    }

    static AdminController get() {
        return Instance.instance;
    }

    Collection<LtrpPlayer> getAdminsOnDuty();
    Collection<LtrpPlayer> getModeratorsOnDuty();

}
