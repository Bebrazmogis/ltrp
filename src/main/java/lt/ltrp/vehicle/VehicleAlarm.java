package lt.ltrp.vehicle;

/**
 * @author Bebras
 *         2015.12.18.
 */
public interface VehicleAlarm {


    int getLevel();

    String getName();
    void setName(String s);

    void activate();
    void deactivate();


}
