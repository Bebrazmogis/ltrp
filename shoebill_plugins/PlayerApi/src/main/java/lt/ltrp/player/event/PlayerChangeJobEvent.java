package lt.ltrp.player.event;



import lt.ltrp.job.object.Job;
import lt.ltrp.player.object.LtrpPlayer;

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
