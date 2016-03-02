package lt.ltrp.job;

/**
 * @author Bebras
 *         2015.12.06.
 */
public interface Rank {

    int getNumber();
    String getName();
    void setJob(Job job);
    Job getJob();
    int getSalary();
    void setSalary(int salary);

}
