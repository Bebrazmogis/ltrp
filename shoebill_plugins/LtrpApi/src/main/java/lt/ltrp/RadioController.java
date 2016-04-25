package lt.ltrp;

import lt.ltrp.dao.RadioStationDao;

/**
 * @author Bebras
 *         2016.04.19.
 */
public interface RadioController {

    static class Instance {
        static RadioController instance;
    }

    static RadioController get() {
        return Instance.instance;
    }

    RadioStationDao getRadioStationDao();

}
