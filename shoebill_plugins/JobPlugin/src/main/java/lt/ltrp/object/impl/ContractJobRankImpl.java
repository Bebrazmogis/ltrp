package lt.ltrp.object.impl;

import lt.ltrp.job.object.ContractJob;
import lt.ltrp.job.object.ContractJobRank;
import lt.ltrp.job.object.JobRank;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bebras
 *         2015.12.07.
 */
public class ContractJobRankImpl extends EntityImpl implements ContractJobRank {

    private int number, xpNeeded,salary;
    private String name;
    private ContractJob job;

    public ContractJobRankImpl(int uuid, ContractJob job, int number, int xpNeeded, String name, int salary) {
        super(uuid);
        this.job = job;
        this.number = number;
        this.xpNeeded = xpNeeded;
        this.name = name;
        this.salary = salary;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getXpNeeded() {
        return xpNeeded;
    }

    public void setXpNeeded(int xpNeeded) {
        this.xpNeeded = xpNeeded;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setJob(ContractJob job) {
        this.job = job;
    }


    @NotNull
    @Override
    public ContractJob getJob() {
        return job;
    }

    @Override
    public int getSalary() {
        return salary;
    }

    @Override
    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public int compareTo(@NotNull JobRank jobRank) {
        return Integer.compare(getNumber(), jobRank.getNumber());
    }

}
