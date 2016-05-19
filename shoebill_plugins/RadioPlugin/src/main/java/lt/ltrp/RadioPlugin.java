package lt.ltrp;

import lt.ltrp.dao.RadioStationDao;
import lt.ltrp.dao.impl.SqlRadioStationDao;
import net.gtaun.shoebill.event.resource.ResourceLoadEvent;
import net.gtaun.shoebill.resource.Plugin;

import javax.sql.DataSource;

/**
 * @author Bebras
 *         2016.04.13.
 */
public class RadioPlugin extends Plugin implements RadioController {

    private RadioStationDao dao;

    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
        getEventManager().registerHandler(ResourceLoadEvent.class,  e-> {
            if(e.getResource().getClass().equals(DatabasePlugin.class)) {
                DataSource ds = ((DatabasePlugin)e.getResource()).getDataSource();
                this.dao = new SqlRadioStationDao(ds);
            }
        });
    }

    @Override
    protected void onDisable() throws Throwable {

    }



    public RadioStationDao getRadioStationDao() {
        return dao;
    }
}
