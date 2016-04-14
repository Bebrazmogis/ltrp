package lt.ltrp.job;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class FactionRank implements Rank {
    
    private int number, salary;
    private String name;
    private Job job;

    public FactionRank(int number, String name, int salary) {
        this.number = number;
        this.name = name;
        this.salary = salary;
    }

    public FactionRank() {

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
