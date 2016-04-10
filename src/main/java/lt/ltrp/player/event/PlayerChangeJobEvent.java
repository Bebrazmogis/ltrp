package lt.ltrp.player.event;

import lt.ltrp.job.Job;
import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
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
