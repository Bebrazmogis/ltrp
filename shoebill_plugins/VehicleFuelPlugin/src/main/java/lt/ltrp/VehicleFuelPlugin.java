package lt.ltrp;

import lt.ltrp.command.FuelStationCommands;
import lt.ltrp.dao.FuelStationDao;
import lt.ltrp.business.dao.impl.MySqlFuelStationDaoImpl;
import lt.ltrp.data.FillData;
import lt.ltrp.data.FuelStation;
import lt.ltrp.dialog.VehicleStationBillDialog;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.player.BankAccount;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.06.06.
 */
public class VehicleFuelPlugin extends Plugin {

    public static final int FUEL_PRICE = 5;

    private Logger logger;
    private EventManagerNode eventManagerNode;
    private Collection<FuelStation> fuelStations;
    private FuelStationDao fuelStationDao;
    private Map<LtrpPlayer, FillData> playerFillData;
    private PlayerCommandManager playerCommandManager;

    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManagerNode = getEventManager().createChildNode();
        this.fuelStations = new ArrayList<>();
        this.playerFillData = new HashMap<>();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            eventManagerNode.registerHandler(ResourceEnableEvent.class, e -> {
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
        eventManagerNode.cancelAll();
        fuelStationDao = new MySqlFuelStationDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource());
        fuelStations.addAll(this.fuelStationDao.get());
        if(fuelStations.size() == 0)
            logger.warn("There are no gas stations.");

        registerEvents();
        registerCommands();
        logger.info(getDescription().getName() + " loaded");
    }

    private void registerEvents() {
        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            FillData fillData = playerFillData.get(player);
            if(fillData != null) {
                playerFillData.remove(player);
                fillData.getTimer().destroy();
            }
        });
    }
    private void registerCommands() {
        this.playerCommandManager = new PlayerCommandManager(eventManagerNode);
        this.playerCommandManager.registerCommands(new FuelStationCommands(this));
        this.playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManagerNode.cancelAll();
        playerFillData.values().forEach(f -> f.getTimer().destroy());
        playerFillData.clear();
        fuelStations.clear();
        playerCommandManager.destroy();
    }

    public void startFillUp(LtrpPlayer player, LtrpVehicle vehicle) {
        Timer timer = Timer.create(400, (i) -> {
            FillData fillData = playerFillData.get(player);
            if(player.getState() != PlayerState.DRIVER)
                endFillUp(player);
            else if(!isInGasStation(player))
                endFillUp(player);
            else if(vehicle.getFuelTank().getFuel() + fillData.getFuel() >=vehicle.getFuelTank().getFuel())
                endFillUp(player);
            else if(vehicle.getState().getEngine() == VehicleParam.PARAM_ON)
                endFillUp(player);
            else {
                int price = Math.round(fillData.getFuel() * FUEL_PRICE);
                BankAccount account = BankPlugin.get(BankPlugin.class).getBankController().getAccount(player);
                if((price > player.getMoney() && account == null) || (price > player.getMoney() && price > account.getMoney()))
                    endFillUp(player);
                else {
                    fillData.addFuel(2f);

                }
            }
        });
        timer.start();
        this.playerFillData.put(player, new FillData(vehicle, player, timer));
    }

    public boolean isInFillUp(LtrpPlayer player) {
        return playerFillData.containsKey(player);
    }

    public void endFillUp(LtrpPlayer player) {
        endFillUp(playerFillData.get(player));
    }

    private void endFillUp(FillData fillData) {
        LtrpPlayer player = fillData.getPlayer();
        fillData.getTimer().stop();
        playerFillData.remove(player);
        VehicleStationBillDialog.create(player, eventManagerNode, fillData)
                .show();
    }

    public boolean isInGasStation(LtrpPlayer player) {
        Location location = player.getLocation();
        return fuelStations.stream()
                .filter(s -> s.getRadius().isInRange(location))
                .findFirst()
                .isPresent();
    }

}
