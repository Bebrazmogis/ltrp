package lt.ltrp;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.event.player.PlayerSpawnSetUpEvent;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.12.
 *
 *
 */

/*

    forward OnPlayerDataLoad(playerid);
    forward OnShoebillPlayerLogin(playerid, failedloginattemps);
    forward OnPlayerSpawnSetUp(playerid);

 */
public class PawnCallbacks  {

    public PawnCallbacks(EventManager manager) {
        System.out.println("PawnCallbacks :: constructor");

        manager.registerHandler(PlayerDataLoadEvent.class, e -> {
            AmxCallable callback = PawnFunc.getPublicMethod("OnPlayerDataLoad");
            if(callback != null) {
                callback.call(e.getPlayer().getId());
            } else {
                System.out.println("PawnCallbacks :: constructor. Pawn code does not contain OnPlayerDataLoad, nothing to call");
            }
        });

        manager.registerHandler(PlayerLogInEvent.class, e -> {
            AmxCallable callback = PawnFunc.getPublicMethod("OnShoebillPlayerLogin");
            if(callback != null) {
                callback.call(e.getPlayer().getId(), e.getFailedAttempts());
            } else {
                System.out.println("PawnCallbacks :: constructor. Pawn code does not contain OnShoebillPlayerLogin, nothing to call");
            }
        });

        manager.registerHandler(PlayerSpawnSetUpEvent.class, e -> {
            AmxCallable callback = PawnFunc.getPublicMethod("OnPlayerSpawnSetUp");
            if(callback != null) {
                callback.call(e.getPlayer().getId());
            } else {
                System.out.println("PawnCallbacks :: constructor. Pawn code does not contain OnPlayerSpawnSetUp, nothing to call");
            }
        });
    }

}
