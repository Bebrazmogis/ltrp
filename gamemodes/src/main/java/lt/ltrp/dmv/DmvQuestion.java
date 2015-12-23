package lt.ltrp.dmv;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class DmvQuestion {

    private int id;
    private String question;
    private DmvAnswer[] answers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public DmvAnswer[] getAnswers() {
        return answers;
    }

    public void setAnswers(DmvAnswer[] answers) {
        this.answers = answers;
    }


    public class DmvAnswer {
        private int id;
        private String answer;
        private boolean correct;

        public DmvAnswer(int id, String answer, boolean correct) {
            this.id = id;
            this.answer = answer;
            this.correct = correct;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }
    }
}

