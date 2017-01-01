package lt.ltrp.vehicle.event;


import lt.ltrp.object.Dmv;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.QuestionTest;

/**
 * @author Bebras
 *         2015.12.25.
 */
public class PlayerQuestionTestEndEvent extends PlayerDmvTestEndEvent {


    public PlayerQuestionTestEndEvent(LtrpPlayer player, Dmv dmv, QuestionTest test) {
        super(player, dmv, test);
    }

    public QuestionTest getTest() {
        return (QuestionTest)super.getTest();
    }

}
