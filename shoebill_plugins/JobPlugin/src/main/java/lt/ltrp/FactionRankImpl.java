package lt.ltrp;

import lt.ltrp.object.Faction;
import lt.ltrp.object.FactionRank;
import lt.ltrp.object.Job;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class FactionRankImpl implements FactionRank {
    
    private int number, salary;
    private String name;
    private Faction job;

    public FactionRankImpl(int number, String name, int salary) {
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setJob(Job job) {
        if(job instanceof Faction)
            setJob((Faction)job);
    }

    @Override
    public void setJob(Faction job) {
        this.job = job;
    }

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
}
