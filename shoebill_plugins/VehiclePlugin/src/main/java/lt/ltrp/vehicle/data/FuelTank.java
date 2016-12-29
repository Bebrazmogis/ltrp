package lt.ltrp.vehicle.data;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class FuelTank {


    private float size, fuel;


    public FuelTank(float size, float fuel) {
        this.size = size;
        this.fuel = fuel;
    }

    public void addFuel(float fuel) {
        this.fuel += fuel;
        if(this.fuel > size)
            this.fuel = size;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public boolean isFull() {
        return this.fuel >= this.size;
    }
}
