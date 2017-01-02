package lt.ltrp.job.vehicle.object.impl;

import lt.ltrp.data.TaxiFare;
import lt.ltrp.job.object.Job;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.job.object.JobVehicle;
import lt.ltrp.job.vehicle.JobVehicleContainer;
import lt.ltrp.vehicle.object.impl.LtrpVehicleImpl;
import net.gtaun.shoebill.data.*;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class JobVehicleImpl extends LtrpVehicleImpl implements JobVehicle {

    private Job job;
    private JobRank rankNeeded;


    public JobVehicleImpl(Job job, int modelId, AngledLocation location, int color1, int color2, JobRank requiredRank, String license, float mileage, EventManager eventManager) {
        this(0, job, modelId, location, color1, color2, requiredRank, license, mileage, eventManager);
    }

    public JobVehicleImpl(int id, Job job, int modelId, AngledLocation spawnLocation, int color1, int color2, JobRank requiredRank, String license, float mileage, EventManager eventManager) {
        super(id, modelId, spawnLocation, color1, color2, null, license, mileage, eventManager);
        this.job = job;
        this.rankNeeded = requiredRank;
    }

    public JobRank getRequiredRank() {
        return rankNeeded;
    }

    public void setRequiredRank(JobRank rankNeeded) {
        this.rankNeeded = rankNeeded;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public void destroy() {
        super.destroy();
        JobVehicleContainer.INSTANCE.remove(this);
    }

}
