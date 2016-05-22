package lt.ltrp.data;


import java.sql.Date;
import java.time.Instant;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class BanData {

    private int uuid;
    private int userId, adminId;
    private String reason, ip;
    private int duration;
    private Date createdAt, deletedAt;

    public BanData(int uuid, int userId, int adminId, String reason, String ip, int hours, Date createdAt, Date deletedAt) {
        this.uuid = uuid;
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.ip = ip;
        this.duration = hours;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public BanData(int userId, int adminId, String reason, String ip, int hours, Date createdAt, Date deletedAt) {
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.ip = ip;
        this.duration = hours;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public BanData(int userId, int adminId, String reason, int hours, Date createdAt, Date deletedAt) {
        this(userId, adminId, reason, null, hours, createdAt, deletedAt);
    }

    public BanData(int userId, int adminId, String reason, Date createdAt, Date deletedAt) {
        this(userId, adminId, reason, null, -1, createdAt, deletedAt);
    }

    public BanData(int userId, int adminId, String reason, String ip, Date createdAt, Date deletedAt) {
        this(userId, adminId, reason, ip, -1, createdAt, deletedAt);
    }

    public int getUUID() {
        return uuid;
    }

    public int getUserId() {
        return userId;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getReason() {
        return reason;
    }

    public String getIp() {
        return ip;
    }

    /**
     *
     * @return returns the duration of this ban in h ours
     */
    public int getDuration() {
        return duration;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Sets the duration of this ban
     * @param duration the new duration in  hours
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isPermanent() {
        return duration == -1;
    }

    public boolean isExpired() {
        return !isPermanent() && (deletedAt != null || Instant.now().toEpochMilli() > createdAt.getTime() + duration * 60 * 60 * 1000);
    }

    public Date getExpirationDate() {
        return new Date(createdAt.getTime() + duration * 60 * 60 * 100);
    }
}
