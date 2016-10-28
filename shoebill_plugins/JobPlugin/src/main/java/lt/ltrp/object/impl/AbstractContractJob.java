package lt.ltrp.object.impl;

import lt.ltrp.job.object.ContractJob;
import lt.ltrp.job.object.ContractJobRank;
import lt.ltrp.job.object.JobRank;
import lt.ltrp.job.object.JobVehicle;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.06.
 */

public abstract class AbstractContractJob extends AbstractJob implements ContractJob {

    private int contractLength;
    private int maxPaycheck;
    private List<? extends ContractJobRank> ranks;

    public AbstractContractJob(int id, String name, Location location, int basePaycheck, EventManager eventManager,
                               int contractLength, int maxPaycheck) {
        super(id, name, location, basePaycheck, eventManager);
        this.contractLength = contractLength;
        this.maxPaycheck = maxPaycheck;
        this.ranks = new ArrayList<>();
    }

    public AbstractContractJob(int uuid, EventManager eventManager) {
        super(uuid, eventManager);
        this.ranks = new ArrayList<>();
    }

    @Override
    public ContractJobRank getRankByNumber(int id) {
        Optional<? extends ContractJobRank> rank = ranks.stream().filter(r -> r.getNumber() == id).findFirst();
        return rank.isPresent() ? rank.get() : null;
    }

    @Nullable
    @Override
    public ContractJobRank getRankByUUID(int uuid) {
        Optional<? extends ContractJobRank> rank = ranks.stream().filter(r -> r.getUUID() == uuid).findFirst();
        return rank.isPresent() ? rank.get() : null;
    }

    @NotNull
    @Override
    public List<? extends ContractJobRank> getRanks() {
        return ranks;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AbstractContractJob && ((AbstractContractJob) o).getUUID() == this.getUUID();
    }


    public int getMaxPaycheck() {
        return maxPaycheck;
    }

    public void setMaxPaycheck(int maxPaycheck) {
        this.maxPaycheck = maxPaycheck;
    }

    public int getContractLength() {
        return contractLength;
    }

    public void setContractLength(int contractLength) {
        this.contractLength = contractLength;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Contract Job. Id %s Name:%s JobRank count:%d vehicle count:%d", getUUID(), getName(), getRanks().size(), getVehicles().size());
    }

    @NotNull
    @Override
    public Collection<JobVehicle> getVehicles(@NotNull ContractJobRank rank) {
        return super.getVehicles().stream().filter(v -> v.getRequiredRank().equals(rank)).collect(Collectors.toSet());
    }
}
