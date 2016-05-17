package lt.ltrp.command;

import lt.ltrp.constant.Currency;
import lt.ltrp.data.BuyBusinessOffer;
import lt.ltrp.data.BuyGarageOffer;
import lt.ltrp.data.BuyHouseOffer;
import lt.ltrp.data.Color;
import lt.ltrp.event.property.BusinessBuyEvent;
import lt.ltrp.event.property.garage.GarageBuyEvent;
import lt.ltrp.event.property.house.HouseBuyEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.Garage;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class PropertyAcceptCommands {

    private EventManager eventManager;

    public PropertyAcceptCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Command
    public boolean business(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
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

    @Command
    public boolean garage(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.containsOffer(BuyBusinessOffer.class))
            player.sendErrorMessage("Jums niekas nesi�lo pirkti gara�o!");
        else {
            BuyGarageOffer offer = player.getOffer(BuyGarageOffer.class);
            Garage garage = offer.getGarage();
            int price = offer.getPrice();
            if(garage.getOwner() != offer.getOfferedBy().getUUID())
                player.sendErrorMessage("�is verslas jau nebeparduodamas, nesp�jote...");
            else if(price > player.getMoney())
                player.sendErrorMessage("Jums neu�tenka pinig� �sigyti �iam gara�ui.");
            else if(!offer.getOfferedBy().isOnline())
                player.sendErrorMessage("Pardav�jas atsijung�, pasi�lymas nebegalioja.");
            else {
                garage.setOwner(player.getUUID());
                player.sendMessage(Color.GARAGE, "S�kmingai nusipirkote gara�� u� " + price + Currency.SYMBOL);
                offer.getOfferedBy().sendMessage(Color.GARAGE, player.getName() + " nupirko j�s� gara��!");
                player.giveMoney(-price);
                offer.getOfferedBy().giveMoney(price);
                eventManager.dispatchEvent(new GarageBuyEvent(garage, offer.getOfferedBy(), player));
            }
            player.getOffers().remove(offer);
        }
        return true;
    }

    @Command
    public boolean house(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.containsOffer(BuyHouseOffer.class))
            player.sendErrorMessage("Jums niekas nesi�lo pirkti namo!");
        else {
            BuyHouseOffer offer = player.getOffer(BuyHouseOffer.class);
            House house = offer.getHouse();
            int price = offer.getPrice();
            if(house.getOwner() != offer.getOfferedBy().getUUID())
                player.sendErrorMessage("�is namas jau nebeparduodamas, nesp�jote...");
            else if(price > player.getMoney())
                player.sendErrorMessage("Jums neu�tenka pinig� �sigyti �iam namui.");
            else if(!offer.getOfferedBy().isOnline())
                player.sendErrorMessage("Pardav�jas atsijung�, pasi�lymas nebegalioja.");
            else {
                house.setOwner(player.getUUID());
                player.sendMessage(Color.GARAGE, "S�kmingai nusipirkote nam� u� " + price + Currency.SYMBOL);
                offer.getOfferedBy().sendMessage(Color.GARAGE, player.getName() + " nupirko j�s� nam�!");
                player.giveMoney(-price);
                offer.getOfferedBy().giveMoney(price);
                eventManager.dispatchEvent(new HouseBuyEvent(house, offer.getOfferedBy(), player));
            }
            player.getOffers().remove(offer);
        }
        return true;
    }

}
