package lt.ltrp.job.object;

import lt.ltrp.job.JobController;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface JobVehicle extends LtrpVehicle {

    public static JobVehicle getById(int id) {
        for(LtrpVehicle veh : LtrpVehicle.get()) {
            if(veh instanceof JobVehicle && veh.getId() == id) {
                return (JobVehicle)veh;
            }
        }
        return null;
    }

    public static JobVehicle getClosest(LtrpPlayer player, float distance) {
        return getClosest(player.getLocation(), distance);
    }
    public static JobVehicle getClosest(Location location, float distance) {
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


    public static JobVehicle create(int id, lt.ltrp.job.object.Job job, int modelId, AngledLocation location, int color1, int color2, Rank requiredRank, String license, float mileage) {
        return JobController.get().createVehicle(id, job, modelId, location, color1, color2, requiredRank, license, mileage);
    }


    Rank getRequiredRank();
    void setRequiredRank(Rank rankNeeded);
    Job getJob();
    void setJob(Job job);


}
