package lt.ltrp.player.event;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerToggleAdminDutyEvent extends PlayerEvent {

    private boolean onDuty;

    public PlayerToggleAdminDutyEvent(LtrpPlayer player, boolean onDuty) {
        super(player);
        this.onDuty = onDuty;
    }

    public boolean isOnDuty() {
        return onDuty;
    }
}
