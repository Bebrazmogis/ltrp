package lt.ltrp.dmv.event;

import lt.ltrp.dmv.Dmv;
import lt.ltrp.dmv.car.DrivingTest;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.25.
 */
public class PlayerDrivingTestEndEvent extends PlayerDmvTestEndEvent {


    public PlayerDrivingTestEndEvent(LtrpPlayer player, Dmv dmv, DrivingTest test) {
        super(player, dmv, test);
    }


    public DrivingTest getTest() {
        return (DrivingTest)super.getTest();
    }
}
