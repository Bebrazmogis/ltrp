package lt.ltrp.data;

import lt.ltrp.player.object.LtrpPlayer;

import java.time.Instant;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class PlayerQuestion {

    private LtrpPlayer player;
    private String question;
    private Instant instant;
    private boolean answered;

    public PlayerQuestion(LtrpPlayer player, String question, Instant instant) {
        this.player = player;
        this.question = question;
        this.instant = instant;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public String getQuestion() {
        return question;
    }

    public Instant getInstant() {
        return instant;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
