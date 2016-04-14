package lt.ltrp.job.event;

import lt.ltrp.job.object.Faction;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class RemoveFactionLeaderEvent extends Event {

    private int leaderUserId;
    private Faction faction;

    public RemoveFactionLeaderEvent(int leaderUserId, Faction faction) {
        this.leaderUserId = leaderUserId;
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }

    public int getLeaderUserId() {
        return leaderUserId;
    }
}
