package lt.ltrp.dmv;

import net.gtaun.shoebill.data.Location;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.25.
 */
public abstract class QuestionDmvImpl extends DmvImpl implements QuestionDmv {

    private List<DmvQuestion> questions;

    public QuestionDmvImpl(Dmv dmv, List<DmvQuestion> questions) {
        super(dmv);
        this.questions = questions;
    }

    public QuestionDmvImpl(int id, String name, Location location, List<DmvQuestion> questions) {
        super(id, name, location);
        this.questions = questions;
    }


    @Override
    public List<DmvQuestion> getQuestions() {
        return questions;
    }

    @Override
    public void setQuestions(List<DmvQuestion> questions) {
        this.questions = questions;
    }
}
