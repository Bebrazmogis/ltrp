package lt.ltrp.object;

import lt.ltrp.data.PhoneBook;
import lt.ltrp.data.PhoneCall;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface ItemPhone extends Item {

    public static final int NUMBER_MIN = 86000000;
    public static final int NUMBER_MAX = 87000000;

    PhoneCall getPhonecall();
    void setPhonecall(PhoneCall phonecall);
    int getPhonenumber();
    void setPhonenumber(int phonenumber);
    PhoneBook getPhoneBook();
    boolean isBusy();
    void setBusy(boolean busy);
    boolean showSmsInbox(LtrpPlayer player, Inventory inventory);
    boolean showSmsOutbox(LtrpPlayer player, Inventory inventory);
    boolean showPhoenbook(LtrpPlayer player, Inventory inventory);
    void initiateCall(LtrpPlayer player, Inventory inventory, int phonenumber);
    void sendSms(LtrpPlayer player, Inventory inventory, int phonenumber, String messagetext);



}
