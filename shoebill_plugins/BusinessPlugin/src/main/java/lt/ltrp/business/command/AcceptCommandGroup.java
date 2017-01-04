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
            player.sendErrorMessage("Jums niekas nesi�lo pirkti verslo!");
        else {
            BuyBusinessOffer offer = player.getOffer(BuyBusinessOffer.class);
            Business business = offer.getBusiness();
            int price = offer.getPrice();
            if(business.getOwner() != offer.getOfferedBy().getUUID())
                player.sendErrorMessage("�is verslas jau nebeparduodamas, nesp�jote...");
            else if(price > player.getMoney())
                player.sendErrorMessage("Jums neu�tenka pinig� �sigyti �iam verslui.");
            else if(!offer.getOfferedBy().isOnline())
                player.sendErrorMessage("Pardav�jas atsijung�, pasi�lymas nebegalioja.");
            else {
                business.setOwner(player.getUUID());
                player.sendMessage(Color.BUSINESS, "S�kmingai nusipirkote " + business.getName() + " u� " + price + Currency.SYMBOL);
                offer.getOfferedBy().sendMessage(Color.BUSINESS, player.getName() + " nupirko j�s� versl�!");
                player.giveMoney(-price);
                offer.getOfferedBy().giveMoney(price);
                eventManager.dispatchEvent(new BusinessBuyEvent(business, player));
            }
            player.getOffers().remove(offer);
        }
        return true;
    }

}
