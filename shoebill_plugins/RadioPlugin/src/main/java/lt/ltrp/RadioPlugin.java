package lt.ltrp;

import lt.ltrp.dao.RadioStationDao;
import net.gtaun.shoebill.resource.Plugin;

/**
 * @author Bebras
 *         2016.04.13.
 */
public class RadioPlugin extends Plugin implements RadioController {

    private RadioStationDao dao;

    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
    }

    @Override
    protected void onDisable() throws Throwable {

    }



    public RadioStationDao getRadioStationDao() {
        return dao;
    }
}
