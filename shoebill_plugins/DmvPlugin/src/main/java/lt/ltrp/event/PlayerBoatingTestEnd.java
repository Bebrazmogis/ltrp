package lt.ltrp.event;

import lt.ltrp.boat.BoatingTest;
import lt.ltrp.object.Dmv;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class PlayerBoatingTestEnd extends PlayerDmvTestEndEvent {

    public PlayerBoatingTestEnd(LtrpPlayer player, Dmv dmv, BoatingTest test) {
        super(player, dmv, test);
    }

    public BoatingTest getTest() {
        return (BoatingTest)super.getTest();
    }
}
