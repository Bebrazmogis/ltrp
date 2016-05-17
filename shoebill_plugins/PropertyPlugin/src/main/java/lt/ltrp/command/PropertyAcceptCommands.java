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

    @Command
    public boolean garage(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!player.containsOffer(BuyBusinessOffer.class))
            player.sendErrorMessage("Jums niekas nesiûlo pirkti garaþo!");
        else {
            BuyGarageOffer offer = player.getOffer(BuyGarageOffer.class);
            Garage garage = offer.getGarage();
            int price = offer.getPrice();
            if(garage.getOwner() != offer.getOfferedBy().getUUID())
                player.sendErrorMessage("Ðis verslas jau nebeparduodamas, nespëjote...");
            else if(price > player.getMoney())
                player.sendErrorMessage("Jums neuþtenka pinigø ásigyti ðiam garaþui.");
            else if(!offer.getOfferedBy().isOnline())
                player.sendErrorMessage("Pardavëjas atsijungë, pasiûlymas nebegalioja.");
            else {
                garage.setOwner(player.getUUID());
                player.sendMessage(Color.GARAGE, "Sëkmingai nusipirkote garaþà uþ " + price + Currency.SYMBOL);
                offer.getOfferedBy().sendMessage(Color.GARAGE, player.getName() + " nupirko jûsø garaþà!");
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
            player.sendErrorMessage("Jums niekas nesiûlo pirkti namo!");
        else {
            BuyHouseOffer offer = player.getOffer(BuyHouseOffer.class);
            House house = offer.getHouse();
            int price = offer.getPrice();
            if(house.getOwner() != offer.getOfferedBy().getUUID())
                player.sendErrorMessage("Ðis namas jau nebeparduodamas, nespëjote...");
            else if(price > player.getMoney())
                player.sendErrorMessage("Jums neuþtenka pinigø ásigyti ðiam namui.");
            else if(!offer.getOfferedBy().isOnline())
                player.sendErrorMessage("Pardavëjas atsijungë, pasiûlymas nebegalioja.");
            else {
                house.setOwner(player.getUUID());
                player.sendMessage(Color.GARAGE, "Sëkmingai nusipirkote namà uþ " + price + Currency.SYMBOL);
                offer.getOfferedBy().sendMessage(Color.GARAGE, player.getName() + " nupirko jûsø namà!");
                player.giveMoney(-price);
                offer.getOfferedBy().giveMoney(price);
                eventManager.dispatchEvent(new HouseBuyEvent(house, offer.getOfferedBy(), player));
            }
            player.getOffers().remove(offer);
        }
        return true;
    }

}
