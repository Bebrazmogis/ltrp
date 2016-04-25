package lt.ltrp.data;


import java.sql.Timestamp;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class LicenseWarning {

    private int id;
    private PlayerLicense license;
    private String body, issuedBy;
    private Timestamp date;


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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
