package lt.ltrp.event;

import lt.ltrp.data.Vote;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.06.13.
 */
public abstract class VoteEvent extends Event {

    private Vote vote;

    public VoteEvent(Vote vote) {
        this.vote = vote;
    }

    public Vote getVote() {
        return vote;
    }
}
