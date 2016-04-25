package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface MedicFaction extends Faction {

    class Instance {
        static MedicFaction instance;
    }

    static MedicFaction get() {
        return Instance.instance;
    }

}
