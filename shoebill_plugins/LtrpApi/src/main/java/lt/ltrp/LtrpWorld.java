package lt.ltrp;

import lt.ltrp.data.Taxes;

/**
 * @author Bebras
 *         2016.04.16.
 */
public class LtrpWorld {

    private static final LtrpWorld instance = new LtrpWorld();

    public static LtrpWorld get() {
        return instance;
    }

    private Taxes taxes;
    private int money;

    private LtrpWorld() {

        this.taxes = new Taxes(20, 80, 10, 1, 21);
    }

    public Taxes getTaxes() {
        return taxes;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int amount) {
        this.money = amount;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

}
