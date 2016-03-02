package lt.ltrp.job;

/**
 * @author Bebras
 *         2015.12.07.
 */
public class ContractJobRank implements Rank {

    private int number, xpNeeded,salary;
    private String name;
    private Job job;

    public ContractJobRank(int number, int xpNeeded, String name, int salary) {
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
        this.job = job;
    }

    @Override
    public Job getJob() {
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
