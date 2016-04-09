package lt.ltrp.vehicle;

import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.job.Rank;
import lt.ltrp.job.Job;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class JobVehicle extends LtrpVehicle {

    public static JobVehicle getById(int id) {
        for(LtrpVehicle veh : JobVehicle.get()) {
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
        for(LtrpVehicle v : get()) {
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

    public static JobVehicle create(Job job, int modelid, AngledLocation location, int color1, int color2, Rank requiredrank, float mileage) {
        return create(0, job, modelid, location, color1, color2, requiredrank, job.getName().substring(0, 3) + modelid, mileage);
    }

    public static JobVehicle create(int id, Job job, int modelid, AngledLocation location, int color1, int color2, Rank requiredrank, String license, float mileage) {
        JobVehicle veh =  new JobVehicle(id, job, modelid, location, color1, color2, requiredrank, license, mileage);
        logger.debug("Creating job vehicle  " + veh.getId());
        return veh;
    }



    private Job job;
    private Rank rankNeeded;


    private JobVehicle(Job job, int modelid, AngledLocation location, int color1, int color2, Rank requiredrank, String license, float mileage) {
        this(0, job, modelid, location, color1, color2, requiredrank, license, mileage);
    }

    private JobVehicle(int id, Job job, int modelid, AngledLocation spawnLocation, int color1, int color2, Rank requiredrank, String license, float mileage) {
        super(id, modelid, spawnLocation, color1, color2, license, mileage);
        this.job = job;
        this.rankNeeded = requiredrank;
    }

    public Rank getRequiredRank() {
        return rankNeeded;
    }

    public void setRequiredRank(Rank rankNeeded) {
        this.rankNeeded = rankNeeded;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
