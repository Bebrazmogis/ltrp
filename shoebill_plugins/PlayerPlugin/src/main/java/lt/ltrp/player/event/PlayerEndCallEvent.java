package lt.ltrp.player.event;


import lt.ltrp.data.PhoneCall;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.03.23.
 */
public class PlayerEndCallEvent extends PlayerEvent {

    private PhoneCall call;

    public PlayerEndCallEvent(LtrpPlayer player, PhoneCall call) {
        super(player);
        this.call = call;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public PhoneCall getCall() {
        return call;
    }
}
