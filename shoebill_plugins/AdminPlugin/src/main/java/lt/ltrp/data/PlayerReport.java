package lt.ltrp.data;

import lt.ltrp.object.LtrpPlayer;

import java.time.Instant;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class PlayerReport {

    private LtrpPlayer player;
    private LtrpPlayer target;
    private String reason;
    private Instant instant;
    private boolean answered;

    public PlayerReport(LtrpPlayer player, LtrpPlayer target, String reason, Instant instant) {
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.instant = instant;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public LtrpPlayer getTarget() {
        return target;
    }

    public String getReason() {
        return reason;
    }

    public Instant getInstant() {
        return instant;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
