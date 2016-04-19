package lt.ltrp.mechanic;

import lt.ltrp.colorpicker.ColorPicker;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class MechanicAcceptCommands {

    private EventManager eventManager;

    public MechanicAcceptCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }


    @Command
    @CommandHelp("Priema pasi�lym� per�velgti spalv� palet�")
    public boolean palette(Player p) {
        System.out.println("plaette called");
        LtrpPlayer player = LtrpPlayer.get(p);
        ColorPalettePlayerOffer offer = player.getOffer(ColorPalettePlayerOffer.class);
        System.out.println("palette offer:" + offer);
        if(offer == null) {
            player.sendErrorMessage("Jums niekas nesi�lo per�i�r�t spalv� palet�s.");
        } else {
            player.getOffers().remove(offer);
            offer.getOfferedBy().sendMessage(player.getCharName() + " priem� j�s� pasi�lym� per�i�r�ti spalv� palet�.");
            ColorPicker.create(player, eventManager, color -> {
                player.sendActionMessage("i�sirenka spalv� i� spalv� palet�s, kurios kodas yra #" + color);
            });

        }
        return true;
    }


}
