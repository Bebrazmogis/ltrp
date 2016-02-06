package lt.ltrp.player;

import java.util.Date;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class LicenseWarning {

    private int id;
    private PlayerLicense license;
    private String body, issuedBy;
    private Date date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlayerLicense getLicense() {
        return license;
    }

    public void setLicense(PlayerLicense license) {
        this.license = license;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
