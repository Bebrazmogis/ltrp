package lt.ltrp;

import lt.ltrp.command.BusinessCommands;
import lt.ltrp.command.BusinessSetSpawnCommand;
import lt.ltrp.constant.BusinessType;
import lt.ltrp.dao.BusinessDao;
import lt.ltrp.dao.impl.MySqlBusinessDaoImpl;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.dialog.AdminBusinessManagementDialog;
import lt.ltrp.dialog.BusinessAvailableCommodityManagementDialog;
import lt.ltrp.event.property.*;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.BusinessImpl;
import lt.ltrp.util.StringUtils;
import lt.maze.streamer.event.PlayerDynamicPickupEvent;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class BusinessPlugin extends Plugin implements BusinessController {

    private Logger logger;
    private Collection<Business> businessCollection;
    private MySqlBusinessDaoImpl businessDao;
    private EventManagerNode node;

    @Override
    protected void onEnable() throws Throwable {
        BusinessController.Instance.instance = this;
        logger = getLogger();
        businessCollection = new ArrayList<>();
        node = getEventManager().createChildNode();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(PropertyPlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            node.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                logger.debug("R:" + r);
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    logger.debug("Removing r");
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
    }
    private void load() {
        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        node.cancelAll();
        businessDao = new MySqlBusinessDaoImpl(databasePlugin.getDataSource(), node);
        businessCollection.addAll(businessDao.get());
        addEventHandlers();
        addCommands();
        logger.info(getDescription().getName()  + " loaded");
    }

    private void addCommands() {
        PlayerCommandManager playerCommandManager = new PlayerCommandManager(node);
        playerCommandManager.registerCommands(new BusinessCommands(node));
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        SpawnPlugin.get(SpawnPlugin.class).getSetSpawnCommandGroup().registerCommands(new BusinessSetSpawnCommand());
    }

    private void addEventHandlers() {
        node.registerHandler(BusinessEvent.class, e -> {
            new Thread(() -> {
                Business b = e.getProperty();
                businessDao.update(b);
                b.getCommodities().forEach(businessDao::update);
            }).start();
        });

        node.registerHandler(BusinessNameChangeEvent.class, e -> {
            Business b = e.getProperty();
            businessDao.update(b);
            LtrpPlayer.sendAdminMessage(e.getPlayer() + " pakeitë verslo " + b.getUUID() + " pavadinimà á " + b.getName());
        });
        node.registerHandler(BusinessBuyEvent.class, HandlerPriority.HIGHEST, e -> {
            businessDao.update(e.getProperty());
        });

        node.registerHandler(BusinessCreateEvent.class, HandlerPriority.HIGHEST, e -> {
            Business b = e.getProperty();
          //  businessDao.insert(b);
        });

        node.registerHandler(BusinessNameChangeEvent.class, e -> {
            businessDao.update(e.getProperty());
        });

        node.registerHandler(BusinessBankChangeEvent.class, e -> {
            Business b = e.getProperty();
            businessDao.update(b);
            LtrpPlayer p = e.getPlayer();
            if(p != null) {
                // TODO log
            }
        });

        node.registerHandler(BusinessEvent.class, e -> {
            System.out.println("BusinessEvent");
            businessDao.update(e.getProperty());
        });

        node.registerHandler(BusinessEditEvent.class, e -> {
            System.out.println("BusinessEditEvent");
            businessDao.update(e.getProperty());
        });

        node.registerHandler(BusinessDestroyEvent.class, HandlerPriority.HIGHEST, e -> {
            //businessDao.remove(e.getProperty());
            businessCollection.remove(e.getProperty());
        });


        node.registerHandler(PlayerDynamicPickupEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            Optional<Business> opB = Business.get().stream().filter(b -> b.getPickup() != null && b.getPickup().equals(e.getPickup())).findFirst();
            if(opB.isPresent()) {
                Business b = opB.get();
                String name = StringUtils.limit(StringUtils.replaceLtChars(StringUtils.stripColors(b.getName())), 40, "..");
                String msg = String.format("%s~n~~w~Savininkas: ~g~%s~n~ ~w~Mokestis: ~g~ %d ~n~~p~ Noredami ieiti - Rasykite /enter",
                        name, PlayerController.get().getUsernameByUUID(b.getOwner()), b.getEntrancePrice());
                player.sendGameText(6000, 7, msg);
                // TODO rasi alternatyva ivietoj gameText. Su ilgaisp avadinimais blogai
            }
        });

        //commodities
        node.registerHandler(BusinessCommodityAddEvent.class, e -> {
            businessDao.insert(e.getCommodity());
        });

        node.registerHandler(BusinessCommodityRemoveEvent.class, e -> {
            businessDao.remove(e.getCommodity());
        });

        node.registerHandler(BusinessCommodityPriceUpdateEvent.class, e -> {
            businessDao.update(e.getCommodity());
        });

    }

    @Override
    protected void onDisable() throws Throwable {
        businessCollection.forEach(Business::destroy);
        businessCollection.clear();
        businessCollection = null;
        node.cancelAll();
    }


    @Override
    public Business get(LtrpPlayer ltrpPlayer) {
        Optional<Business> biz = getBusinesses().stream().filter(h -> h.getExit() != null && h.getExit().distance(ltrpPlayer.getLocation()) < 200f).findFirst();
        return biz.isPresent() ? biz.get() : null;
    }

    @Override
    public Business createBusiness(int i, String s, BusinessType businessType, int i1, int i2, int i3, Location location, Location location1, Color color, int i4, int i5, int i6) {
        Business b = new BusinessImpl(i, s, businessType, i1, i2, i3, location, location1, color, i4, i5, i6, node);
        businessDao.insert(b);
        businessCollection.add(b);
        return b;
    }

    @Override
    public Collection<Business> getBusinesses() {
        return businessCollection;
    }

    @Override
    public List<BusinessCommodity> getAvailableCommodities(BusinessType businessType) {
        return businessDao.get(businessType);
    }

    @Override
    public BusinessDao getBusinessDao() {
        return businessDao;
    }

    @Override
    public void showManagementDialog(LtrpPlayer ltrpPlayer) {
        AdminBusinessManagementDialog.create(ltrpPlayer, node)
                .show();
    }

    @Override
    public void showAvailableCommodityDialog(LtrpPlayer ltrpPlayer) {
        BusinessAvailableCommodityManagementDialog.create(ltrpPlayer, node).show();
    }

    @Override
    public Business getClosest(Location location, float v) {
        Business closest = null;
        float min = v;
        for (Business b : getBusinesses()) {
            float distance = Math.min(b.getEntrance().distance(location), b.getExit() != null ? b.getExit().distance(location) : Float.POSITIVE_INFINITY);
            if(distance <= min) {
                closest = b;
                min = distance;
            }
        }
        return closest;
    }

}
