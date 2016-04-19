package lt.ltrp.medic;


import lt.ltrp.AbstractJobManager;
import lt.ltrp.LoadingException;
import lt.ltrp.JobController;
import lt.ltrp.RoadblockCommands;
import lt.ltrp.object.MedicFaction;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class MedicManager extends AbstractJobManager {


    private MedicFaction job;
    private Collection<LtrpPlayer> medicsOnDuty;
    private PlayerCommandManager playerCommandManager;

    public MedicManager(EventManager eventManager, int id) throws LoadingException {
        super(eventManager);
        this.medicsOnDuty = new ArrayList<>();
        this.job = JobController.get().getDao().getMedicJob(id);

        this.eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(p != null) {
                medicsOnDuty.remove(p);
            }
        });

        this.playerCommandManager = new PlayerCommandManager(eventManagerNode);
        this.playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        this.playerCommandManager.registerCommands(new MedicCommands(job, this));
        this.playerCommandManager.registerCommands(new RoadblockCommands(job, eventManagerNode));

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
    public void destroy() {
        playerCommandManager.destroy();
        medicsOnDuty.clear();
    }

    @Override
    public MedicFaction getJob() {
        return job;
    }
}
