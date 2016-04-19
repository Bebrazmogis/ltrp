package lt.ltrp;

import lt.ltrp.constant.Currency;
import lt.ltrp.dao.BankDao;
import lt.ltrp.dialog.BankAccountDialog;
import lt.ltrp.event.*;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.BankAccount;
import lt.maze.streamer.event.PlayerDynamicPickupEvent;
import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankController implements Destroyable {

    private EventManagerNode node;
    private DynamicPickup accountPickup, paycheckPickup;
    private BankDao bankDao;
    private int interest;
    private Collection<SoftReference<BankAccount>> bankAccountCache;
    private int newAccountPrice;

    public BankController(EventManager eventManager, BankDao bankDao) {
        this.bankAccountCache = new ArrayList<>();
        this.node =  eventManager.createChildNode();
        this.bankDao = bankDao;
        this.interest = bankDao.getInterest();
        this.newAccountPrice = bankDao.getNewAccountPrice();

        //new Location(295.772f, 1021.8f, 2123.61f, 0, 0)
        //new Location(298.23f, 1021.78f, 2123.61f, 0, 0)
        accountPickup = DynamicPickup.create(1239, 1, bankDao.getBankLocation());
        paycheckPickup = DynamicPickup.create(1210, 1, bankDao.getPaycheckLocation());

        node.registerHandler(PlayerDynamicPickupEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            DynamicPickup pickup = e.getPickup();
            if(accountPickup.equals(pickup)) {
                BankAccountDialog.create(player, eventManager, this)
                        .show();
            } else if(paycheckPickup.equals(pickup)) {
                if(player.getTotalPaycheck() > 0) {
                    player.giveMoney(player.getTotalPaycheck());
                    player.sendMessage(Color.BISQUE, "Pasiemëte sukauptà algà. " + Currency.SYMBOL + player.getTotalPaycheck());
                    node.dispatchEvent(new PlayerTakePaycheckEvent(player, player.getTotalPaycheck()));
                    player.setTotalPaycheck(0);
                } else {
                    player.sendErrorMessage("Jûs jau esate pasiemæs algà.");
                }
            }
        });

        node.registerHandler(BankAccountCreateEvent.class, e -> {
            bankDao.insertAccount(e.getBankAccount());
            e.getPlayer().sendMessage(Color.BISQUE, "Sveikiname atsidarius sàskità. Jos numeris " + e.getBankAccount().getNumber());
        });

        node.registerHandler(BankAccountDepositMoney.class, e -> {
            bankDao.updateAccount(e.getAccount());
            e.getPlayer().sendMessage(Color.BISQUE, Currency.SYMBOL + e.getAmount() + " sëkmingai padëti. Naujas balansas " + Currency.SYMBOL + e.getAccount().getMoney());
        });

        node.registerHandler(BankAccountWithdrawMoney.class, e -> {
            bankDao.updateAccount(e.getAccount());
            e.getPlayer().sendMessage(Color.BISQUE, Currency.SYMBOL + e.getAmount() + " sëkmingai paimti. Naujas balansas " + Currency.SYMBOL + e.getAccount().getMoney());
        });

        node.registerHandler(BankPlaceDepositEvent.class, e -> {
            bankDao.updateAccount(e.getAccount());
            e.getPlayer().sendMessage(Color.BISQUE, String.format("Indëlis padëtas. %c%d. Palûkanos: %d%%", Currency.SYMBOL, e.getAmount(), e.getInterest()));
        });

        node.registerHandler(BankTakeDepositEvent.class, e -> {
            bankDao.updateAccount(e.getAccount());
            e.getPlayer().sendMessage(Color.BISQUE, String.format("Pasiemëte indëlá. Suma: %c%d", Currency.SYMBOL, e.getDeposit()));
        });

        node.registerHandler(BankWireTransferEvent.class, e -> {
            BankAccount account = e.getAccount();
            LtrpPlayer player = e.getPlayer();
            BankAccount targetAccount = getAccount(e.getRecipientNumber());
            if(targetAccount != null) {
                account.wire(targetAccount, e.getAmount());
                player.sendMessage(Color.BISQUE, "Pavedimas " + targetAccount.getNumber() + " sëkmingai ávykdytas. Suma " + Currency.SYMBOL + e.getAmount());
                bankDao.updateAccount(account);
                bankDao.updateAccount(targetAccount);
            } else {
                player.sendErrorMessage("Sàskaitos su tokiu numeriu nëra!");
            }
        });
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
        bankDao.setInterest(interest);
    }

    public void update(BankAccount acc) {
        bankDao.updateAccount(acc);
    }

    public String generateAccountNumber(LtrpPlayer player) {
        String number = null;
        while(number == null || getAccount(number) == null) {
            number = String.format("%s%06d", LtrpGamemode.NameShort, player.getUUID() + player.getId());
        }
        return number;
    }

    public int getAccountPrice() {
        return newAccountPrice;
    }

    public void setNewAccountPrice(int newAccountPrice) {
        this.newAccountPrice = newAccountPrice;
        bankDao.setNewAccountPrice(newAccountPrice);
    }

    public BankAccount getAccount(LtrpPlayer player) {
        for(SoftReference<BankAccount> softAccount : bankAccountCache) {
            BankAccount account = softAccount.get();
            if(account != null && account.getUserId() == player.getUUID()) {
                return account;
            }
        }
        return bankDao.getAccount(player);
    }

    public BankAccount getAccount(String accountNnumber) {
        if(accountNnumber == null)
            return null;
        for(SoftReference<BankAccount> softAccount : bankAccountCache) {
            BankAccount account = softAccount.get();
            if(account != null && account.getNumber().equals(accountNnumber)) {
                return account;
            }
        }
        return bankDao.getAccount(accountNnumber);
    }


    @Override
    public void destroy() {
        node.cancelAll();
        node.destroy();
        accountPickup.destroy();
        paycheckPickup.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

}
