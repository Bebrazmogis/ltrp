package lt.ltrp.vehicle.data;

import java.sql.Timestamp;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class PlayerVehicleArrest {

    private int id;
    private int vehicleId;
    private int arrestedBy;
    private String reason;
    private Timestamp date;

    public PlayerVehicleArrest(int id, int vehicleId, int arrestedBy, String reason, Timestamp date) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.arrestedBy = arrestedBy;
        this.reason = reason;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public int getArrestedBy() {
        return arrestedBy;
    }

    public String getReason() {
        return reason;
    }


    public Timestamp getDate() {
        return date;
    }
}
