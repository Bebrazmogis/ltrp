package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface DrugDealerJob extends ContractJob {

    class Instance {
        static DrugDealerJob instance;
    }

    static DrugDealerJob get() {
        return Instance.instance;
    }

    int getSeedPrice();

}
