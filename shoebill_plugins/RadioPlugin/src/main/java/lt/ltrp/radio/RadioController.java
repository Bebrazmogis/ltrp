package lt.ltrp.radio;

import lt.ltrp.radio.dao.RadioStationDao;
import net.gtaun.shoebill.resource.Plugin;

/**
 * @author Bebras
 *         2016.04.13.
 */
public class RadioController extends Plugin {

    private static RadioController instance;

    private RadioStationDao dao;

    @Override
    protected void onEnable() throws Throwable {
        instance = this;
    }

    @Override
    protected void onDisable() throws Throwable {

    }


    public static RadioController getController() {
        return instance;
    }

    public static RadioStationDao getRadioStationDao() {
        return instance.dao;
    }
}
