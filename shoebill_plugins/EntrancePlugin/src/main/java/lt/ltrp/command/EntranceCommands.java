package lt.ltrp.command;

import lt.ltrp.JobController;
import lt.ltrp.event.PlayerEnterEntranceEvent;
import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceCommands {

    private EventManager eventManager;

    public EntranceCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }


    @Command
    public boolean enter(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        Entrance entrance = Entrance.getClosest(player.getLocation(), 6f);
        if(entrance == null)
            return false;

        if(entrance.getExit() == null)
            player.sendErrorMessage("Èia nëra kur áeiti.");
        if(!entrance.allowsVehicles() && player.isInAnyVehicle())
            player.sendErrorMessage("Bûdamas transporto priemonëje áeiti negalite!");
        if(!entrance.getJob().equals(JobController.get().getJob(player)))
            player.sendErrorMessage("Tai yra tarnybinis áëjimas, jûs èia nedirbate.");
        else {
            Entrance exit = entrance.getExit();
            if(player.isInAnyVehicle()) {
                LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                vehicle.setLocation(exit.getLocation());
                LtrpPlayer.get().stream().filter(p -> p.getVehicle().equals(vehicle)).forEach(p -> {
                    p.setInterior(exit.getLocation().getInteriorId());
                    p.setWorld(exit.getLocation().getWorldId());
                });
            } else {
                player.setLocation(exit.getLocation());
            }
            eventManager.dispatchEvent(new PlayerEnterEntranceEvent(exit, player));
        }
        return true;
    }

    @Command
    public boolean exit(Player p) {
        return enter(p);
    }
}
