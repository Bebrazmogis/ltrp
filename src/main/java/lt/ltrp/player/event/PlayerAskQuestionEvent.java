package lt.ltrp.player.event;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerAskQuestionEvent extends PlayerEvent {

    private String question;

    public PlayerAskQuestionEvent(LtrpPlayer player, String question) {
        super(player);
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}
