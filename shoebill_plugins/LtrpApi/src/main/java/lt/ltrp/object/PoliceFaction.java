package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.04.14.
 */
public interface PoliceFaction extends Faction {

    class Instance {
        static PoliceFaction instance;
    }

    static PoliceFaction get() {
        return Instance.instance;
    }

}
