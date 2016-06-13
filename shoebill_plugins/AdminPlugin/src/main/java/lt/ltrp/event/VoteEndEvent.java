package lt.ltrp.event;

import lt.ltrp.data.Vote;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class VoteEndEvent extends VoteEvent {

    public VoteEndEvent(Vote vote) {
        super(vote);
    }
}
