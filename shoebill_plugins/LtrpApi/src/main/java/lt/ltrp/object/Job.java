package lt.ltrp.object;


import lt.ltrp.NamedEntity;
import lt.ltrp.JobController;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;
import java.util.List;
/**
 * @author Bebras
 *         2015.12.03.
 */
public interface Job extends NamedEntity {

    Location getLocation();
    void setLocation(Location loc);
    Collection<? extends Rank> getRanks();
    Rank getRank(int id);
    void setRanks(Collection<? extends Rank> ranks);
    Collection<JobVehicle> getVehicles();
    void setVehicles(Collection<JobVehicle> vehicles);
    Collection<JobVehicle> getVehicles(Rank rank);
    void addVehicle(JobVehicle vehicle);
    int getBasePaycheck();
    void setBasePaycheck(int paycheck);

    /**
     * Checks if a player is at a workplace
     * @param player
     * @return true if the player is considered to be at work, false otherwise
     */
    boolean isAtWork(LtrpPlayer player);


    static List<Job> get() {
        return JobController.get().getJobs();
    }

    static Job get(int id) {
        return JobController.get().getJob(id);
    }


    /**
     * Sends a message to all connected players that belong to this job
     * @param color message color
     * @param message message text
     */
    void sendMessage(Color color, String message);


}

