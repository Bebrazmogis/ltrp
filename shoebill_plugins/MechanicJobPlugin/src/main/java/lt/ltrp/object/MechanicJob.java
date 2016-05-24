package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface MechanicJob extends ContractJob {

    class Instance {
        static MechanicJob instance;
    }

    static MechanicJob get() {
        return Instance.instance;
    }

   int getHydraulicsInstallPrice();
   int getHydraulicRemovePrice();
   int getWheelPrice();
}
