package lt.ltrp.data;

import java.util.Date;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PhoneSms {

    private int id;
    private int senderNumber, recipientNumber;
    private Date date;
    private String text;
    private boolean read;

    public PhoneSms(int id, int senderNumber, int recipientNumber, Date date, String text, boolean read) {
        this.id = id;
        this.senderNumber = senderNumber;
        this.recipientNumber = recipientNumber;
        this.date = date;
        this.text = text;
        this.read = read;
    }

    public PhoneSms(int senderNumber, int recipientNumber, Date date, String text) {
        this.senderNumber = senderNumber;
        this.recipientNumber = recipientNumber;
        this.date = date;
        this.text = text;
    }

    public int getSenderNumber() {
        return senderNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSenderNumber(int senderNumber) {
        this.senderNumber = senderNumber;
    }

    public int getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(int recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
