package lt.ltrp.dao;

import lt.ltrp.data.PhoneContact;
import lt.ltrp.data.PhoneSms;
import lt.ltrp.data.Phonebook;

/**
 * @author Bebras
 *         2015.11.29.
 */
public interface PhoneDao {

    public void addSms(PhoneSms sms);
    public PhoneSms[] getSmsBySender(int phonenumber);
    public PhoneSms[] getSmsByRecipient(int phonenumber);
    public void update(PhoneSms sms);


    public Phonebook getPhonebook(int phonenumber);
    public PhoneContact add(int ownernumber, int contactnumber, String name);
    public void update(PhoneContact contact);
    public void remove(PhoneContact contact);

    public void logConversation(int phonenumberfrom, int phonenumberto, String text);

}
