package lt.ltrp.player;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankAccount {

    private int id;
    private String number;
    private int userId;
    private int money;
    private int deposit;
    private int interest;
    private Timestamp depositTimestamp;

    public BankAccount(int id, String number, int userId, int money, int deposit, int interest, Timestamp timestamp) {
        this.id = id;
        this.number = number;
        this.userId = userId;
        this.money = money;
        this.deposit = deposit;
        this.interest = interest;
        this.depositTimestamp = timestamp;
    }

    public BankAccount(String number, int userId) {
        this.number = number;
        this.userId = userId;
    }

    public void wire(BankAccount account, int amount) {
        if(amount <= money) {
            this.money -= amount;
            account.addMoney(amount);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }


    public void placeDeposit(int amount, int interest) {
        this.money -= amount;
        this.deposit = amount;
        this.interest = interest;
        this.depositTimestamp = new Timestamp(new Date().getTime());
    }

    public void takeDeposit() {
        this.money += deposit;
        this.deposit = 0;
        this.interest = 0;
        this.depositTimestamp = null;
    }

    public int getInterest() {
        return interest;
    }

    public Timestamp getDepositTimestamp() {
        return depositTimestamp;
    }

    public void setDeposit(int amount) {
        this.deposit = amount;
    }

    public int getDeposit() {
        return deposit;
    }

    public String getNumber() {
        return number;
    }

    public int getUserId() {
        return userId;
    }

    public int getMoney() {
        return money;
    }
}
