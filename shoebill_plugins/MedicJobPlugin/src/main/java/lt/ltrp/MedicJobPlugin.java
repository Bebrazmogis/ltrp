package lt.ltrp;

import lt.ltrp.command.MedicCommands;
import lt.ltrp.dao.MedicFactionDao;
import lt.ltrp.dao.impl.MySqlMedicFactionDaoImpl;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.MedicFaction;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MedicJobPlugin extends Plugin {

    private EventManagerNode eventManager;
    private Logger logger;
    private MedicFactionDao medicFactionDao;
    private MedicFaction medicFaction;
    private PlayerCommandManager playerCommandManager;
    private Collection<LtrpPlayer> medicsOnDuty;


    @Override
    protected void onEnable() throws Throwable {
        this.medicsOnDuty = new ArrayList<>();
        logger = getLogger();
        eventManager = getEventManager().createChildNode();


        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(JobPlugin.class);
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
        this.medicFactionDao = new MySqlMedicFactionDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), null, eventManager);
        this.medicFaction = medicFactionDao.get(JobPlugin.JobId.Medic.id);
        registerCommands();
        addEventHandlers();
        logger.info(getDescription().getName() + " loaded");
    }

    private void addEventHandlers() {
        this.eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(p != null) {
                medicsOnDuty.remove(p);
            }
        });
    }

    private void registerCommands() {
        this.playerCommandManager = new PlayerCommandManager(eventManager);
        this.playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        this.playerCommandManager.registerCommands(new MedicCommands(medicFaction, eventManager));
        this.playerCommandManager.registerCommands(new RoadblockCommands(medicFaction, eventManager));
    }

    public Collection<LtrpPlayer> getMedicsOnDuty() {
        return medicsOnDuty;
    }

    public void setOnDuty(LtrpPlayer player, boolean set) {
        if(set)
            medicsOnDuty.add(player);
        else
            medicsOnDuty.remove(player);
    }

    public boolean isOnDuty(LtrpPlayer player){
        return medicsOnDuty.contains(player);
    }


    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        playerCommandManager.uninstallAllHandlers();
        logger.info(getDescription().getName() + " unloaded");
    }
}
