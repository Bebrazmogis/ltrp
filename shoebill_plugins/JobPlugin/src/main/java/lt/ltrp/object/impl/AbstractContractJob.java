package lt.ltrp.object.impl;

import lt.ltrp.object.ContractJob;
import lt.ltrp.object.ContractJobRank;
import lt.ltrp.object.JobVehicle;
import lt.ltrp.object.Rank;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.06.
 */

public abstract class AbstractContractJob extends AbstractJob implements ContractJob {

    public static final int INVALID_ID = 0;

    private int contractLength;
    private int maxPaycheck, minPaycheck;
    private Collection<ContractJobRank> ranks;
    private Collection<JobVehicle> vehicles;

    public AbstractContractJob(int id, String name, Location location, int basePaycheck, EventManager eventManager,
                               int contractLength, int maxPaycheck, int minPaycheck) {
        super(id, name, location, basePaycheck, eventManager);
        this.contractLength = contractLength;
        this.maxPaycheck = maxPaycheck;
        this.minPaycheck = minPaycheck;
        vehicles = new ArrayList<>();
    }

    public AbstractContractJob(EventManager eventManager) {
        super(eventManager);
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
    public Collection<JobVehicle> getVehicles(Rank rank) {
        return getVehicles().stream().filter(v -> v.getRequiredRank().equals(rank)).collect(Collectors.toList());
    }

    @Override
    public void setVehicles(Collection<JobVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public Collection<JobVehicle> getVehicles(ContractJobRank rank) {
        return vehicles.stream().filter(v -> v.getRequiredRank().equals(rank)).collect(Collectors.toList());
    }

    @Override
    public void addVehicle(JobVehicle vehicle) {
        vehicles.add(vehicle);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AbstractContractJob && ((AbstractContractJob) o).getUUID() == this.getUUID();
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
        return super.toString() + String.format("Contract Job. Id %s Name:%s Rank count:%d vehicle count:%d", getUUID(), getName(), getRanks().size(), vehicles.size());
    }
}
