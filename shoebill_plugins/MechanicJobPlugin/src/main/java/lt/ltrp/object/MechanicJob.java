package lt.ltrp.object;

import lt.ltrp.job.object.ContractJob;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface MechanicJob extends ContractJob {

   int getHydraulicsInstallPrice();
   int getHydraulicRemovePrice();
   int getWheelPrice();
}
