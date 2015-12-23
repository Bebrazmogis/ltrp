package lt.ltrp.vehicle;

/**
 * @author Bebras
 *         2015.12.18.
 */
public class VehicleLock {



    private int level, crackTime, price;
    private String name;

    public VehicleLock(String name, int crackTime, int price) {
        this.name = name;
        this.crackTime = crackTime;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCrackTime(int crackTime) {
        this.crackTime = crackTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCrackTime() {
        return crackTime;
    }
}
