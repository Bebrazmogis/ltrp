package lt.ltrp.vehicle;

import java.util.Date;

/**
 * @author Bebras
 *         2016.01.03.
 */
public class VehicleCrime {

    private String licensePlate;
    private Date date;
    private String reporter;
    private String crime;
    private int fine, id;

    public VehicleCrime(int id, String licensePlate, Date date, String reporter, String crime, int fine) {
        this.licensePlate = licensePlate;
        this.date = date;
        this.reporter = reporter;
        this.crime = crime;
        this.fine = fine;
        this.id = id;
    }

    public VehicleCrime(String licensePlate, String reporter, String crime, int fine) {
        this.licensePlate = licensePlate;
        this.reporter = reporter;
        this.crime = crime;
        this.fine = fine;
        this.date = new Date();
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getCrime() {
        return crime;
    }

    public void setCrime(String crime) {
        this.crime = crime;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
