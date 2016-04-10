package lt.ltrp.player.event;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerAcceptPlayerQuestion extends PlayerEvent {

    private LtrpPlayer target;
    private String question;

    public PlayerAcceptPlayerQuestion(LtrpPlayer player, LtrpPlayer target, String question) {
        super(player);
        this.target = target;
        this.question = question;
    }

    public LtrpPlayer getTarget() {
        return target;
    }

    public String getQuestion() {
        return question;
    }

}
