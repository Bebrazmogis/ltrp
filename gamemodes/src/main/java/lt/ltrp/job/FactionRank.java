package lt.ltrp.job;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class FactionRank implements Rank {
    
    private int number;
    private String name;

    public FactionRank(int number, String name) {
        this.number = number;
        this.name = name;
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
}
