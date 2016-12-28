package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.dao.AdvertisementCenterDao;
import lt.ltrp.dao.AdvertisementDao;
import lt.ltrp.business.dao.impl.FileAdvertisementCenterDaoImpl;
import lt.ltrp.business.dao.impl.MySqlAdvertisementDaoImpl;
import lt.ltrp.data.Advert;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.AdvertisementListDialog;
import lt.ltrp.event.PlayerSelectItemOptionEvent;
import lt.ltrp.object.Item;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.NewsPaperItem;
import lt.ltrp.resource.DependentPlugin;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class AdvertPlugin extends DependentPlugin {

    private EventManagerNode eventManager;
    private Location adCenterLocation;
    private AdvertisementCenterDao centerDao;
    private AdvertisementDao advertisementDao;

    public AdvertPlugin() {
        super();
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(ItemPlugin.class));
    }

    @Override
    public void onDependenciesLoaded() {
        this.eventManager = getEventManager().createChildNode();
        DataSource dataSource = ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource();
        this.centerDao = new FileAdvertisementCenterDaoImpl(getDataDir(), getLogger());
        this.advertisementDao = new MySqlAdvertisementDaoImpl(dataSource);

        adCenterLocation = this.centerDao.get();
        if(adCenterLocation == null) {
            getLogger().error("Advertisement center location is null, exitting");
            try {
                disable();
                return;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        this.eventManager.registerHandler(PlayerSelectItemOptionEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            Item item = e.getItem();
            if(item instanceof NewsPaperItem && e.getOption().equals("read")) {
                AdvertisementListDialog.create(player, getEventManager(), getAdsBeforeDate(((NewsPaperItem) item).getDate()))
                        .build()
                        .show();
            }
        });
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        eventManager.cancelAll();
    }


    public Location getAdCenterLocation() {
        if(adCenterLocation == null) adCenterLocation = centerDao.get();
        return adCenterLocation;
    }

    public void setAdCenterLocation(Location adCenterLocation) {
        this.adCenterLocation = adCenterLocation;
        centerDao.set(adCenterLocation);
    }

    public int getAdvertLetterPrice() {
        return 0;
    }

    public void createAdvert(LtrpPlayer player, String advert, int phoneNumber, int price) {
        Advert ad = new Advert(player.getUUID(), phoneNumber, advert, price);
        int uuid = AdvertPlugin.get(AdvertPlugin.class).advertisementDao.insert(ad);
        ad.setUUID(uuid);
        LtrpPlayer.sendGlobalMessage(Color.AD, String.format("[Skelbimas] %s, kontaktai: %d", advert, phoneNumber));
    }

    public Collection<Advert> getAdsBeforeDate(Timestamp timestamp) {
        return advertisementDao.get(timestamp);
    }

    public Collection<Advert> getAds() {
        return advertisementDao.get();
    }
}
