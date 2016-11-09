package lt.ltrp.data;

import java.time.LocalDateTime;

/**
 * @author Bebras
 *         2016.05.20.
 */
public class WarnData {

    private int id;
    private int userId;
    private int warnedByUserId;
    private String reason;
    private LocalDateTime date;

    public WarnData(int id, int userId, int warnedByUserId, String reason, LocalDateTime date) {
        this.id = id;
        this.userId = userId;
        this.warnedByUserId = warnedByUserId;
        this.reason = reason;
        this.date = date;
    }

    public WarnData(int userId, int warnedByUserId, String reason, LocalDateTime date) {
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
