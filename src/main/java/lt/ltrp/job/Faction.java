package lt.ltrp.job;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.List;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class Faction implements Job {

    public static final int INVALID_ID = 0;


    private int id;
    private String name;
    private Location location;
    private List<FactionRank> ranks;
    private int leaderId;
    private Map<FactionRank, JobVehicle> vehicles;


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<FactionRank, JobVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public boolean isAtWork(LtrpPlayer player) {
        return player.getLocation().distance(location) < 50.0f;
    }

    @Override
    public void sendMessage(Color color, String message) {
        for(LtrpPlayer p : LtrpPlayer.get()) {
            if(p.getJob() == this) {
                p.sendMessage(color, message);
            }
        }
    }

    public void setVehicles(Map<FactionRank, JobVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location loc) {
        this.location = loc;
    }

    @Override
    public List<FactionRank> getRanks() {
        return ranks;
    }

    public void setRanks(List<FactionRank> ranks) {
        this.ranks = ranks;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Faction && ((Faction) o).getId() == this.getId();
    }

    @Override
    public FactionRank getRank(int id) {
        for(FactionRank rank : ranks) {
            if(rank.getNumber() == id) {
                return rank;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Faction. Id %s Name:%s Rank count:%d vehicle count:%d", id, getName(), getRanks().size(), vehicles.size());
    }
}
