package lt.ltrp.data;

import java.sql.Timestamp;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PhoneContact {

    private int id;
    private int number;
    private String name;
    private Timestamp date;

    public PhoneContact(int id, int contactNumber, String name, Timestamp date) {
        this.id = id;
        this.number = contactNumber;
        this.name = name;
        this.date = date;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
