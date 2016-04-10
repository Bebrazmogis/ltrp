package lt.ltrp.player.event;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerStateMessageEvent extends PlayerEvent {

    private String text;

    public PlayerStateMessageEvent(LtrpPlayer player, String text) {
        super(player);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return super.toString() + "text=" + text;
    }
}
