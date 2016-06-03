package lt.ltrp.data;

import lt.ltrp.object.Entity;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class Advert implements Entity {

    private int uuid;
    private int authorUserId;
    private int phoneNumber;
    private String adText;
    private int price;
    private Timestamp createdAt;

    public Advert(int uuid, int authorUserId, int phoneNumber, String adText, int price, Timestamp createdAt) {
        this.uuid = uuid;
        this.authorUserId = authorUserId;
        this.phoneNumber = phoneNumber;
        this.adText = adText;
        this.price = price;
        this.createdAt = createdAt;
    }

    public Advert(int authorUserId, int phoneNumber, String adText, int price) {
        this.authorUserId = authorUserId;
        this.phoneNumber = phoneNumber;
        this.adText = adText;
        this.price = price;
        this.createdAt = new Timestamp(Instant.now().toEpochMilli());
    }

    public int getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(int authorUserId) {
        this.authorUserId = authorUserId;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdText() {
        return adText;
    }

    public void setAdText(String adText) {
        this.adText = adText;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setUUID(int i) {
        this.uuid = i;
    }

    @Override
    public int getUUID() {
        return uuid;
    }
}
