package lt.ltrp.event;

import lt.ltrp.dmv.Dmv;
import lt.ltrp.dmv.DmvTest;
import lt.ltrp.dmv.QuestionTest;
import lt.ltrp.object.LtrpPlayer;

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
