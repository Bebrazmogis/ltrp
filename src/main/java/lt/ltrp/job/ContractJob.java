package lt.ltrp.job;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.JobVehicle;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.06.
 */

public class ContractJob implements Job {

    public static final int INVALID_ID = 0;

    private int id;
    private int contractLength;
    private int minPaycheck, maxPaycheck;
    private String name;
    private Location location;
    private Collection<ContractJobRank> ranks;
    private Collection<JobVehicle> vehicles;

    public ContractJob() {
        JobManager.getContractJobs().add(this);
        vehicles = new ArrayList<>();
    }

    public ContractJob(int id) {
        super();
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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
    public Collection<ContractJobRank> getRanks() {
        return ranks;
    }

    @Override
    public ContractJobRank getRank(int id) {
        for(ContractJobRank c : ranks) {
            if(c.getNumber() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void setRanks(Collection<? extends Rank> ranks) {
        this.ranks = (Collection<ContractJobRank>) ranks;
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
        return vehicles.stream().filter(v -> v.getRequiredRank().equals(rank)).collect(Collectors.toList());
    }

    @Override
    public void addVehicle(JobVehicle vehicle) {
        vehicles.add(vehicle);
    }

    @Override
    public int getBasePaycheck() {
        return minPaycheck;
    }

    @Override
    public void setBasePaycheck(int paycheck) {
        this.minPaycheck = paycheck;
    }

    @Override
    public boolean isAtWork(LtrpPlayer player) {
        return this.location.distance(player.getLocation()) < 10.0f;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ContractJob && ((ContractJob) o).getId() == this.getId();
    }

    @Override
    public void sendMessage(Color color, String message) {
        LtrpPlayer.get().stream().filter(p -> p.getJob().equals(this)).forEach(p -> p.sendMessage(color, message));
    }

    public void setRanks(List<ContractJobRank> ranks) {
        this.ranks =  ranks;
    }

    public int getMaxPaycheck() {
        return maxPaycheck;
    }

    public void setMaxPaycheck(int maxPaycheck) {
        this.maxPaycheck = maxPaycheck;
    }

    public int getMinPaycheck() {
        return minPaycheck;
    }

    public void setMinPaycheck(int minPaycheck) {
        this.minPaycheck = minPaycheck;
    }

    public int getContractLength() {
        return contractLength;
    }

    public void setContractLength(int contractLength) {
        this.contractLength = contractLength;
    }

    @Override
    public String toString() {
        return String.format("Contract Job. Id %s Name:%s Rank count:%d vehicle count:%d", id, getName(), getRanks().size(), vehicles.size());
    }
}
