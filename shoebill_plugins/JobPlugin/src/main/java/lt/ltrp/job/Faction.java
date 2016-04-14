package lt.ltrp.job;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class Faction implements Job {

    public static final int INVALID_ID = 0;


    private int id;
    private String name;
    private Location location;
    private Collection<FactionRank> ranks;
    private Collection<Integer> leaderIds;
    private Collection<JobVehicle> vehicles;

    public Faction() {
        JobManager.getFactions().add(this);
        this.leaderIds = new ArrayList<>();
        vehicles = new ArrayList<>();
    }

    public Faction(int id) {
        this();
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<JobVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(Collection<JobVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public Collection<JobVehicle> getVehicles(Rank rank) {
        return vehicles == null ?
                new ArrayList<>(0) :
                vehicles.stream().filter(v -> v.getRequiredRank().equals(rank)).collect(Collectors.toList());
    }

    public void addLeader(int userid) {
        leaderIds.add(userid);
    }

    public Collection<Integer> getLeaders() {
        return leaderIds;
    }

    protected void removeLeader(int userid) {
        leaderIds.remove(userid);
    }

    @Override
    public void addVehicle(JobVehicle vehicle) {
        vehicles.add(vehicle);
    }

    @Override
    public int getBasePaycheck() {
        return 0;
    }

    @Override
    public void setBasePaycheck(int paycheck) {

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
    public Collection<FactionRank> getRanks() {
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
    public void setRanks(Collection<? extends Rank> ranks) {
        this.ranks = (Collection<FactionRank>) ranks;
    }

    @Override
    public String toString() {
        return String.format("Faction. Id %s Name:%s Rank count:%d vehicle count:%d", id, getName(), getRanks().size(), vehicles.size());
    }
}
