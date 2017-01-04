package lt.ltrp.command;

import lt.ltrp.AdvertPlugin;
import lt.ltrp.constant.Currency;
import lt.ltrp.dialog.PlayerPhoneSelectDialog;
import lt.ltrp.object.ItemPhone;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class AdCommands extends Commands {

    private EventManager eventManager;

    public AdCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Command
    @CommandHelp("Paskelbia reklamà")
    public boolean ad(Player p, @CommandParameter(name = "Skelbimas")String advert) {
        LtrpPlayer player = LtrpPlayer.get(p);
        AdvertPlugin advertPlugin = AdvertPlugin.get(AdvertPlugin.class);
        if(advert == null)
            return false;
        else {
            ItemPhone[] phones = player.getInventory().getItems(ItemPhone.class);
            int price = AdvertPlugin.get(AdvertPlugin.class).getAdvertLetterPrice() * advert.length();
            if(phones.length == 0)
                player.sendErrorMessage("Jûs neturite telefono, todël negalite palitki kontaktiniø duomenø skelbimui.");
            else if(price > player.getMoney())
                player.sendErrorMessage("Jums neþtenka pinigø, skelbimo kaina " + price + Currency.SYMBOL);
            else {
                if(phones.length == 1)
                    advertPlugin.createAdvert(player, advert, phones[0].getPhonenumber(), price);
                else
                    PlayerPhoneSelectDialog.create(player, eventManager)
                            .caption("Pasirinkite telefonà kurá norite naudoti reklamai")
                            .onSelectPhone((d, phone) -> advertPlugin.createAdvert(player, advert, phone.getPhonenumber(), price))
                            .build();

            }
        }
        return true;
    }


}
