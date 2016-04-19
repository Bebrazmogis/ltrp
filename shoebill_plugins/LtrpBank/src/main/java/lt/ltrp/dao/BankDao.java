package lt.ltrp.dao;

import lt.ltrp.player.BankAccount;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Location;

import java.io.Closeable;


/**
 * @author Bebras
 *         2016.03.02.
 */
public interface BankDao extends Closeable {

    BankAccount getAccount(LtrpPlayer player);
    BankAccount getAccount(String number);
    void insertAccount(BankAccount account);
    void removeAccount(BankAccount account);
    void updateAccount(BankAccount account);

    void setInterest(int interest);
    int getInterest();

    void setNewAccountPrice(int price);
    int getNewAccountPrice();

    Location getBankLocation();
    Location getPaycheckLocation();

    void logWire(BankAccount from, BankAccount to, int amount);
}
