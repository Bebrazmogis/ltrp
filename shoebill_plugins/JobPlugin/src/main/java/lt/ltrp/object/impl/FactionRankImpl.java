package lt.ltrp.object.impl;

import lt.ltrp.job.object.Faction;
import lt.ltrp.job.object.FactionRank;
import lt.ltrp.job.object.JobRank;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class FactionRankImpl extends EntityImpl implements FactionRank {
    
    private int number, salary;
    private String name;
    private Faction job;

    public FactionRankImpl(int uuid, Faction faction, int number, String name, int salary) {
        super(uuid);
        this.job = faction;
        this.number = number;
        this.name = name;
        this.salary = salary;
    }

    public FactionRankImpl() {

    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
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
    public void setJob(Faction job) {
        this.job = job;
    }

    @NotNull
    @Override
    public Faction getJob() {
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
