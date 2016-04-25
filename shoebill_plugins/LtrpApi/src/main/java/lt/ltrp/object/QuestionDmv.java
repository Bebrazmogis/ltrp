package lt.ltrp.object;

import lt.ltrp.data.dmv.DmvQuestion;
import net.gtaun.util.event.EventManager;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.25.
 */
public interface QuestionDmv extends Dmv {

    List<DmvQuestion> getQuestions();
    void setQuestions(List<DmvQuestion> questions);

    QuestionTest startQuestionTest(LtrpPlayer player, EventManager eventManager);
    int getQuestionTestPrice();

}
