package lt.ltrp.event;

import lt.ltrp.data.Vote;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class VoteStartEvent extends VoteEvent {
    public VoteStartEvent(Vote vote) {
        super(vote);
    }
}
