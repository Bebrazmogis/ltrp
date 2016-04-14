package lt.ltrp.job.object;

import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface Faction extends Job {


    void setId(int id);
    void setName(String name);
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
    int getId();
    String getName();
    Location getLocation();
    void setLocation(Location loc);
    Collection<FactionRank> getRanks();
    void setRanks(List<FactionRank> ranks);
    FactionRank getRank(int id);
   // void setRanks(Collection<FactionRank> ranks);
}
