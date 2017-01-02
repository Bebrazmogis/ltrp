package lt.ltrp.data;

import lt.ltrp.vehicle.event.VoteEndEvent;
import lt.ltrp.vehicle.event.VoteStartEvent;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.timers.TemporaryTimer;
import net.gtaun.util.event.EventManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class Vote {

    private String question;
    private Map<LtrpPlayer, Boolean> playerVotes;
    private Instant startInstant;
    private int duration;
    private boolean ended;

    public Vote(String question, int seconds, EventManager eventManager) {
        this.question = question;
        this.playerVotes = new HashMap<>();
        this.startInstant = Instant.now();
        TemporaryTimer.create(seconds * 1000, 1, (i) -> {
            eventManager.dispatchEvent(new VoteEndEvent(this));
            ended = true;
        }).start();
        eventManager.dispatchEvent(new VoteStartEvent(this));
    }

    public void addVote(LtrpPlayer player, boolean vote) {
        playerVotes.put(player, vote);
    }

    public String getQuestion() {
        return question;
    }

    public Map<LtrpPlayer, Boolean> getPlayerVotes() {
        return playerVotes;
    }

    public boolean voted(LtrpPlayer player) {
        return playerVotes.containsKey(player);
    }

    public int getVoteCount() {
        return playerVotes.size();
    }

    public int getVoteCount(boolean type) {
        return (int)playerVotes.values().stream().filter(t -> t == type).count();
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isEnded() {
        return ended;
    }
}
