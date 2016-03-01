package lt.ltrp.job;

/**
 * @author Bebras
 *         2015.12.07.
 */
public class ContractJobRank implements Rank {

    private int number, hoursNeeded;
    private String name;
    private Job job;

    public ContractJobRank(int number, int hoursNeeded, String name) {
        this.number = number;
        this.hoursNeeded = hoursNeeded;
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getHoursNeeded() {
        return hoursNeeded;
    }

    public void setHoursNeeded(int hoursNeeded) {
        this.hoursNeeded = hoursNeeded;
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


}
