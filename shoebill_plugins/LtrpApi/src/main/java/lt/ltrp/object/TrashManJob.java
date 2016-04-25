package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface TrashManJob extends ContractJob {

    class Instance {
        static TrashManJob instance;
    }

    static TrashManJob get() {
        return Instance.instance;
    }

    int getTrashMasterCapacity();
    int getTrashRouteBonus();
}
