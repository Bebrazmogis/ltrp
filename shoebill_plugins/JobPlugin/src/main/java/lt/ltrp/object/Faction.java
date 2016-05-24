package lt.ltrp.object;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface Faction extends Job {


    Collection<JobVehicle> getVehicles();
    void setVehicles(Collection<JobVehicle> vehicles);
    Collection<JobVehicle> getVehicles(FactionRank rank);
    void addLeader(int uuid);
    Collection<Integer> getLeaders();
     void removeLeader(int uuid); // WAS protected
    void addVehicle(JobVehicle vehicle);
    int getBasePaycheck();
    void setBasePaycheck(int paycheck);
    boolean isAtWork(LtrpPlayer player);
    void sendMessage(Color color, String message);
    Location getLocation();
    void setLocation(Location loc);
    Collection<FactionRank> getRanks();
    FactionRank getRank(int id);
    //void setRanks(Collection<? extends FactionRank> ranks);
}
