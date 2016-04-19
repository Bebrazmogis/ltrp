package lt.ltrp;

import lt.ltrp.object.Faction;
import lt.ltrp.object.FactionRank;
import lt.ltrp.object.JobVehicle;
import lt.ltrp.object.Rank;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.06.
 */
public abstract class AbstractFaction extends AbstractJob implements Faction {

    public static final int INVALID_ID = 0;

    private Collection<FactionRank> ranks;
    private Collection<Integer> leaderIds;
    private Collection<JobVehicle> vehicles;

    public AbstractFaction(int id, String name, Location location, int basePaycheck, EventManager eventManager) {
        super(id, name, location, basePaycheck, eventManager);
    }

    public AbstractFaction(EventManager eventManager) {
        super(eventManager);
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
        return rank instanceof FactionRank ? getVehicles((FactionRank)rank) : new ArrayList<>();
    }

    @Override
    public Collection<JobVehicle> getVehicles(FactionRank rank) {
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

    public void removeLeader(int userid) {
        leaderIds.remove(userid);
    }

    @Override
    public void addVehicle(JobVehicle vehicle) {
        vehicles.add(vehicle);
    }


    @Override
    public Collection<FactionRank> getRanks() {
        return ranks;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Faction && ((Faction) o).getUUID() == this.getUUID();
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
    public void setRanks(Collection<? extends Rank> collection) {
        if(ranks == null) {
            ranks = new ArrayList<>();
        }
        collection.stream().filter(r -> r instanceof FactionRank).forEach(r -> ranks.add((FactionRank)r));
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Faction. Id %s Name:%s Rank count:%d vehicle count:%d", getUUID(), getName(), getRanks().size(), vehicles.size());
    }
}
