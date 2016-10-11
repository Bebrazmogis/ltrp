package lt.ltrp;

import lt.ltrp.dao.AdvertisementCenterDao;
import lt.ltrp.dao.AdvertisementDao;
import lt.ltrp.dao.impl.FileAdvertisementCenterDaoImpl;
import lt.ltrp.dao.impl.MySqlAdvertisementDaoImpl;
import lt.ltrp.data.Advert;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.AdvertisementListDialog;
import lt.ltrp.event.PlayerSelectItemOptionEvent;
import lt.ltrp.object.Item;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.NewsPaperItem;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class AdvertPlugin extends Plugin {

    private EventManagerNode eventManager;
    private Location adCenterLocation;
    private AdvertisementCenterDao centerDao;
    private AdvertisementDao advertisementDao;

    @Override
    protected void onEnable() throws Throwable {
        this.eventManager = getEventManager().createChildNode();


        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(ItemPlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            eventManager.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();

    }

    private void load() {
        eventManager.cancelAll();
        DataSource dataSource = ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource();
        this.centerDao = new FileAdvertisementCenterDaoImpl(getDataDir());
        this.advertisementDao = new MySqlAdvertisementDaoImpl(dataSource);

        adCenterLocation = this.centerDao.get();

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
    protected void onDisable() throws Throwable {
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
