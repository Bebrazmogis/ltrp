package lt.ltrp.player.command;

import lt.ltrp.command.Commands;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.data.PlayerFriskOffer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.entities.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class PlayerAcceptOffers extends Commands {


    @Command
    public boolean frisk(Player p, @CommandParameter(name = "�aid�jo ID")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        Collection<PlayerFriskOffer> offers = target.getOffers(PlayerFriskOffer.class);
        if(offers.size() == 0)
            player.sendErrorMessage("J�s� niekas nenori apie�koti.");
        else {
            Optional<PlayerFriskOffer> optionalOffer = offers.stream().filter(of -> of.getOfferedBy().equals(target)).findFirst();
            if(!optionalOffer.isPresent())
                player.sendErrorMessage(target.getName() + " nenori j�s� apie�koti");
            else {
                PlayerFriskOffer offer = optionalOffer.get();
                if(offer.isExpired())
                    player.sendErrorMessage("Pasi�lymas nebegalioja");
                else if(!offer.getOfferedBy().getPlayer().isOnline())
                    player.sendErrorMessage("�aid�jas atsijung�, pasi�lymas nebegalioja");
                else if(player.getPlayer().getLocation().distance(offer.getOfferedBy().getPlayer().getLocation()) > 5f)
                    player.sendErrorMessage("�aid�jas pasitrauk� per toli kad gal�t� jus apie�koti");
                else {
                    target.sendMessage(Color.GREEN, "_____________________ Turimi daiktai __________________");
                    target.sendMessage(Color.WHITE, "Turimi pinigai: " + player.getMoney() + Currency.SYMBOL);
                    for(LtrpWeaponData wep : player.getWeapons()) {
                        target.sendMessage(Color.WHITE, String.format("Ginklas %s, �ovini� %d", wep.getWeaponData().model.modelName, wep.getWeaponData().ammo));
                    }
                    /*for(Item item : player.getInventory().getItems()) {
                        target.sendMessage(item.getName() + " " + item.getAmount());
                    }*/
                }
                player.getOffers().remove(offer);
            }
        }
        return true;
    }


}
