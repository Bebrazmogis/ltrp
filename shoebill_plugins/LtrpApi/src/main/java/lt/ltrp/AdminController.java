package lt.ltrp;

import lt.ltrp.object.LtrpPlayer;

import java.util.Collection;
import java.util.Map;

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
    Map<LtrpPlayer, String> getPendingPlayerQuestions();
    Collection<LtrpPlayer> getModeratorsOnDuty();

}
