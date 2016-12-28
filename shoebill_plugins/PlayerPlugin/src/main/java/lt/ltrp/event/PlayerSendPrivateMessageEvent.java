package lt.ltrp.event;


import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerSendPrivateMessageEvent extends PlayerEvent {

    private LtrpPlayer target;
    private String message;

    public PlayerSendPrivateMessageEvent(LtrpPlayer player, LtrpPlayer target, String message) {
        super(player);
        this.target = target;
        this.message = message;
    }

    public LtrpPlayer getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return super.toString() + "target=" + target + "message=" + message;
    }
}
