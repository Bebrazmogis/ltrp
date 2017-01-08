package lt.ltrp.business.command;

import lt.ltrp.constant.Currency;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;

/**
 * @author Bebras
 *         2016.12.29.
 */
public class AcceptCommandGroup {

    @Command
    public boolean business(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        if(!player.containsOffer(BuyBusinessOffer.class))
            player.sendErrorMessage("Jums niekas nesiûlo pirkti verslo!");
        else {
            BuyBusinessOffer offer = player.getOffer(BuyBusinessOffer.class);
            Business business = offer.getBusiness();
            int price = offer.getPrice();
            if(business.getOwner() != offer.getOfferedBy().getUUID())
                player.sendErrorMessage("Ðis verslas jau nebeparduodamas, nespëjote...");
            else if(price > player.getMoney())
                player.sendErrorMessage("Jums neuþtenka pinigø ásigyti ðiam verslui.");
            else if(!offer.getOfferedBy().isOnline())
                player.sendErrorMessage("Pardavëjas atsijungë, pasiûlymas nebegalioja.");
            else {
                business.setOwner(player.getUUID());
                player.sendMessage(Color.BUSINESS, "Sëkmingai nusipirkote " + business.getName() + " uþ " + price + Currency.SYMBOL);
                offer.getOfferedBy().sendMessage(Color.BUSINESS, player.getName() + " nupirko jûsø verslà!");
                player.giveMoney(-price);
                offer.getOfferedBy().giveMoney(price);
                eventManager.dispatchEvent(new BusinessBuyEvent(business, player));
            }
            player.getOffers().remove(offer);
        }
        return true;
    }

}
