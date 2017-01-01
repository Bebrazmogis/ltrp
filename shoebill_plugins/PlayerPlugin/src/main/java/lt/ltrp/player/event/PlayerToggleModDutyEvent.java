package lt.ltrp.event.player;


import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerToggleModDutyEvent extends PlayerEvent {

    private boolean status;


    public PlayerToggleModDutyEvent(LtrpPlayer player, boolean status) {
        super(player);
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
