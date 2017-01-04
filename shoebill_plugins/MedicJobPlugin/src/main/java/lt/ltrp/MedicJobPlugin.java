package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.DepartmentChatCommand;
import lt.ltrp.command.MedicCommands;
import lt.ltrp.command.RoadblockCommands;
import lt.ltrp.dao.MedicFactionDao;
import lt.ltrp.business.dao.impl.MySqlMedicFactionDaoImpl;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.MedicFaction;
import lt.ltrp.object.impl.MedicFactionImpl;
import lt.ltrp.resource.DependentPlugin;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class MedicJobPlugin extends DependentPlugin {

    private EventManagerNode eventManager;
    private MedicFactionDao medicFactionDao;
    private MedicFaction medicFaction;
    private PlayerCommandManager playerCommandManager;
    private Collection<LtrpPlayer> medicsOnDuty;

    public MedicJobPlugin() {
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(JobPlugin.class));
    }

    @Override
    public void onDependenciesLoaded() {
        this.medicsOnDuty = new ArrayList<>();
        eventManager = getEventManager().createChildNode();

        this.medicFactionDao = new MySqlMedicFactionDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), eventManager);
        this.medicFaction = new MedicFactionImpl(JobPlugin.JobId.Medic.id, eventManager);
        registerCommands();
        addEventHandlers();
        getLogger().info(getDescription().getName() + " loaded");
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
        this.playerCommandManager.registerCommands(new DepartmentChatCommand(medicFaction));
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
    protected void onDisable() {
        super.onDisable();
        eventManager.cancelAll();
        playerCommandManager.uninstallAllHandlers();
    }
}
