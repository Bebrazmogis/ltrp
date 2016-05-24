package lt.ltrp.event;

import lt.ltrp.session.AbstractRepairSession;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2016.02.09.
 */
public class RepairSessionEndEvent extends PlayerEvent {

    private AbstractRepairSession session;
    private boolean finished;

    public RepairSessionEndEvent(LtrpPlayer player, AbstractRepairSession session, boolean finished) {
        super(player);
        this.session = session;
        this.finished = finished;
    }
    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer) super.getPlayer();
    }

    public AbstractRepairSession getSession() {
        return session;
    }

    public boolean isFinished() {
        return finished;
    }
}
