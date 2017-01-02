package lt.ltrp.job.object;

import lt.ltrp.job.JobController;
import lt.ltrp.job.JobVehicleController;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface JobVehicle extends LtrpVehicle {

    static JobVehicle getById(int id) {
        for(LtrpVehicle veh : LtrpVehicle.get()) {
            if(veh instanceof JobVehicle && veh.getId() == id) {
                return (JobVehicle)veh;
            }
        }
        return null;
    }

    static JobVehicle getClosest(LtrpPlayer player, float distance) {
        return getClosest(player.getLocation(), distance);
    }
    static JobVehicle getClosest(Location location, float distance) {
        JobVehicle vehicle = null;
        for(LtrpVehicle v : LtrpVehicle.get()) {
            if(!(v instanceof JobVehicle))
                continue;
            float dis = location.distance(v.getLocation());
            if(dis < distance) {
                vehicle = (JobVehicle)v;
                distance = dis;
            }
        }
        return vehicle;
    }


    static JobVehicle create(int id, Job job, int modelId, AngledLocation location, int color1, int color2, JobRank requiredRank, String license, float mileage) {
        return JobVehicleController.instance.create(id, job, modelId, location, color1, color2, requiredRank, license, mileage);
    }

    static JobVehicle create(Job job, int modelId, AngledLocation location, int color1, int color2, JobRank requiredRank, float mileage) {
        return create(0, job, modelId, location, color1, color2, requiredRank, job.getName().substring(0, 3) + modelId, mileage);
    }

    JobRank getRequiredRank();
    void setRequiredRank(JobRank rankNeeded);
    Job getJob();
    void setJob(Job job);


}
