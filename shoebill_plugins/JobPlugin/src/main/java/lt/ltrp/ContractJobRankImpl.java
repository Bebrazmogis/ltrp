package lt.ltrp;

import lt.ltrp.object.ContractJob;
import lt.ltrp.object.ContractJobRank;
import lt.ltrp.object.Job;

/**
 * @author Bebras
 *         2015.12.07.
 */
public class ContractJobRankImpl implements ContractJobRank {

    private int number, xpNeeded,salary;
    private String name;
    private ContractJob job;

    public ContractJobRankImpl(int number, int xpNeeded, String name, int salary) {
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

    @Override
    public void setJob(ContractJob contractJob) {
        this.job = contractJob;
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
        if(job instanceof ContractJob)
            setJob((ContractJob)job);
    }


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
}
