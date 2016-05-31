package lt.ltrp.data;

import lt.ltrp.object.Entity;

import java.sql.Timestamp;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class VehicleFine implements Entity {

    private int uuid;
    private int vehicleUUID;
    private String license;
    private String crime;
    private String reportedBy;
    private Timestamp createdAt;
    private Timestamp paidAt;
    private int fine;

    public VehicleFine(int uuid, int vehicleUUID, String license, String crime, String reportedBy, Timestamp createdAt, Timestamp paidAt, int fine) {
        this.uuid = uuid;
        this.vehicleUUID = vehicleUUID;
        this.license = license;
        this.crime = crime;
        this.reportedBy = reportedBy;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.fine = fine;
    }

    public VehicleFine(int vehicleUUID, String license, String crime, String reportedBy, Timestamp createdAt, int fine) {
        this.vehicleUUID = vehicleUUID;
        this.license = license;
        this.crime = crime;
        this.reportedBy = reportedBy;
        this.createdAt = createdAt;
        this.fine = fine;
    }

    public VehicleFine() {
    }

    public int getVehicleUUID() {
        return vehicleUUID;
    }

    public void setVehicleUUID(int vehicleUUID) {
        this.vehicleUUID = vehicleUUID;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getCrime() {
        return crime;
    }

    public void setCrime(String crime) {
        this.crime = crime;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    @Override
    public void setUUID(int i) {
        this.uuid = i;
    }

    @Override
    public int getUUID() {
        return uuid;
    }

    public boolean isPaid() {
        return paidAt != null;
    }
}
