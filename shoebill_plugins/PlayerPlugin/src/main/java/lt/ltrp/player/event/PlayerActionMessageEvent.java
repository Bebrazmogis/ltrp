package lt.ltrp.player.event;


import lt.ltrp.object.LtrpPlayer;

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

    @Override
    public String toString() {
        return super.toString() + String.format("text=%s", text);
    }
}
