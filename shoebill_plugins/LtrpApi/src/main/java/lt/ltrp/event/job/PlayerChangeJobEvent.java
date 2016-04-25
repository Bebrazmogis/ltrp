package lt.ltrp.event.job;



import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.Job;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class PlayerChangeJobEvent extends PlayerEvent {

    private Job oldJob;
    private Job newJob;

    public PlayerChangeJobEvent(LtrpPlayer player, Job oldJob, Job newJob) {
        super(player);
        this.oldJob = oldJob;
        this.newJob = newJob;
    }

    public Job getOldJob() {
        return oldJob;
    }

    public Job getNewJob() {
        return newJob;
    }
}
