package lt.ltrp.dmv;


import lt.ltrp.dmv.event.PlayerQuestionTestEndEvent;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class QuestionTest implements DmvTest {

    public static final int PRICE = 100;

	private LtrpPlayer player;
	private QuestionDmv dmv;
	private boolean passed, finished;
    private EventManager eventManager;
	// Implementation of Map which replaces old values with new and maintains insertion order is required for this class to work as intended.
	private LinkedHashMap<DmvQuestion, Boolean> answeredQuestions;

    public static QuestionTest create(LtrpPlayer player, QuestionDmv dmv, EventManager eventManager) {
        return new QuestionTest(player, dmv, eventManager);
    }
	
	private QuestionTest(LtrpPlayer p, QuestionDmv dmv, EventManager manager) {
		this.player = p;
		this.dmv = dmv;
		this.answeredQuestions = new LinkedHashMap<>();
        this.eventManager = manager;

        showDialog();
	}
	
	private void showDialog() {
		this.showDialog(null);
	}
	
	private void showDialog(DmvQuestion question) {
		final DmvQuestion nextQuestion;
		if(question == null) {
			nextQuestion = getNextQuestion();
		} else {
			nextQuestion = question;
		}
		
		if(nextQuestion == null) {
			stop();
		} else {
			InputDialog.create(player, eventManager)
				.caption(dmv.getName())
				.message("if(answered.contains) ")
				.buttonOk("Tęsti")
				.buttonCancel("Atgal")
				.onClickCancel(dialog -> {
					// iterate over answered questions to find the last one
					Iterator<DmvQuestion> it = answeredQuestions.keySet().iterator();
					DmvQuestion prevQuestion = null;
					while(it.hasNext()) {
						prevQuestion = it.next();
					}
					// if it ie the first question, there won't be a previous one so we show the current one
					if(prevQuestion == null) {
						dialog.show();
					} else {
						showDialog(prevQuestion);
					}
				})
				.onClickOk((dialog, input) -> {
					int answer = -1;
					try {
						answer = Integer.parseInt(input);
					} catch(NumberFormatException ignored) {}
					if(answer == -1) {
						player.sendErrorMessage("");
						dialog.show();
					} else {
						if(answer < 1 ||answer > nextQuestion.getAnswers().length) {
							player.sendErrorMessage("  ");
							dialog.show();
						} else {
							// players are allowed to revisit already answered questions, so an implementation that replace old values must be used.
							answeredQuestions.put(nextQuestion, nextQuestion.getAnswers()[answer - 1].isCorrect());
							showDialog();
						}
					}
				})
				.build()
				.show();
		}
	}
	
	private DmvQuestion getNextQuestion() {	
		DmvQuestion q = null;
		Random random = new Random();
		List<DmvQuestion> questions = dmv.getQuestions();
		// if all questions have been answered, there's nothing to search for
		if(questions.size() == answeredQuestions.keySet().size()) {
			return null;
		}
		do {
			int index = random.nextInt(questions.size());
			q = questions.get(index);
		} while(q == null || answeredQuestions.containsKey(q));
        return q;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions.size();
    }

    @Override
    public LtrpPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isPassed() {
        return passed;
    }

    @Override
    public void stop() {
        if(answeredQuestions.size() == 0) {
            passed = false;
        } else if(answeredQuestions.size() != dmv.getQuestions().size()) {
            passed = false;
        } else {
            passed = true;
        }

        eventManager.dispatchEvent(new PlayerQuestionTestEndEvent(player, dmv, this));
    }
}