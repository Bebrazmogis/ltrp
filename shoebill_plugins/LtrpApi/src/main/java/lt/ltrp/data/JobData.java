package lt.ltrp.data;

import lt.ltrp.object.Job;
import lt.ltrp.object.Rank;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.14.
 */
public class JobData {


    private LtrpPlayer player;
    private Job job;
    private Rank jobRank;
    private int level;
    private int xp;
    private int hours;
    private int remainingContract;

    public JobData(LtrpPlayer player, Job job, Rank jobRank) {
        this.player = player;
        this.job = job;
        this.jobRank = jobRank;
    }

    public JobData() {
    }

    public int getRemainingContract() {
        return remainingContract;
    }

    public void setRemainingContract(int remainingContract) {
        this.remainingContract = remainingContract;
    }

    public JobData(LtrpPlayer player, Job job, Rank jobRank, int level, int xp, int hours, int remainingContract) {
        this.player = player;
        this.job = job;
        this.jobRank = jobRank;
        this.level = level;
        this.xp = xp;
        this.hours = hours;
        this.remainingContract = remainingContract;

    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Rank getJobRank() {
        return jobRank;
    }

    public void setJobRank(Rank jobRank) {
        this.jobRank = jobRank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int xp) {
        this.xp += xp;
    }

    public void addHours(int hours) {
        setHours(getHours() + hours);
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
