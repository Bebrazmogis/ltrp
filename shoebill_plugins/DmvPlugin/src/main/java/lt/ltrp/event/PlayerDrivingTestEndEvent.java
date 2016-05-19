package lt.ltrp.event;

import lt.ltrp.car.DrivingTest;
import lt.ltrp.object.Dmv;
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
