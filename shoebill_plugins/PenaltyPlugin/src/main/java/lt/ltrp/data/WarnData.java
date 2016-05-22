package lt.ltrp.data;

import java.sql.Date;

/**
 * @author Bebras
 *         2016.05.20.
 */
public class WarnData {

    private int id;
    private int userId;
    private int warnedByUserId;
    private String reason;
    private Date date;

    public WarnData(int id, int userId, int warnedByUserId, String reason, Date date) {
        this.id = id;
        this.userId = userId;
        this.warnedByUserId = warnedByUserId;
        this.reason = reason;
        this.date = date;
    }

    public WarnData(int userId, int warnedByUserId, String reason, Date date) {
        this.userId = userId;
        this.warnedByUserId = warnedByUserId;
        this.reason = reason;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWarnedByUserId() {
        return warnedByUserId;
    }

    public void setWarnedByUserId(int warnedByUserId) {
        this.warnedByUserId = warnedByUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
