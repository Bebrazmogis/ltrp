package lt.ltrp.player.event;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerActionMessageEvent extends PlayerEvent{

    private String text;

    public PlayerActionMessageEvent(LtrpPlayer player, String text) {
        super(player);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
