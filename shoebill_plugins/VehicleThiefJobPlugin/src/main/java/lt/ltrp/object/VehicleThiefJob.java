package lt.ltrp.object;


import lt.ltrp.data.NamedLocation;
import lt.ltrp.job.object.ContractJob;

import java.util.Map;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface VehicleThiefJob extends ContractJob {

    void addBuyPoint(NamedLocation location, int model);
    void addBuyPoint(NamedLocation location);
    Map<NamedLocation, Integer> getVehicleBuyPoints();
    int[] getRequiredModels();
    void setRequiredModels(int[] requiredModels);
    int getRequiredModelCount();
    void setRequiredModelCount(int requiredModelCount);
    void resetRequiredModels();
    void log(String s);

}
