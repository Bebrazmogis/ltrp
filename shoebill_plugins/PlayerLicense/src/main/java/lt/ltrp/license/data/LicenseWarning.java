package lt.ltrp.license.data;


import java.time.LocalDateTime;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class LicenseWarning {

    private int id;
    private PlayerLicense license;
    private String body, issuedBy;
    private LocalDateTime date;


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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof LicenseWarning && ((LicenseWarning)object).id == id;
    }
}
