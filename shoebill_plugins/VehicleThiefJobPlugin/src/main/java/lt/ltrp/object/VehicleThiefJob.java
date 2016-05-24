package lt.ltrp.object;


import lt.ltrp.data.NamedLocation;

import java.util.Map;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface VehicleThiefJob extends ContractJob {

    class Instance {
        static VehicleThiefJob instance;
    }

    static VehicleThiefJob get() {
        return Instance.instance;
    }


    public void addBuyPoint(NamedLocation location, int model);
    public void addBuyPoint(NamedLocation location);
    public Map<NamedLocation, Integer> getVehicleBuyPoints();
    public int[] getRequiredModels();
    public void setRequiredModels(int[] requiredModels);
    public int getRequiredModelCount();
    public void setRequiredModelCount(int requiredModelCount);
    public void resetRequiredModels();
    void log(String s);

}
