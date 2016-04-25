package lt.ltrp.event.player;


import lt.ltrp.object.LtrpPlayer;

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
