package lt.ltrp.property.command;

import lt.ltrp.LtrpWorld;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.BuyBusinessOffer;
import lt.ltrp.data.Color;
import lt.ltrp.event.property.BusinessBuyEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class PropertyCommands {

    private EventManager eventManager;

    public PropertyCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }
/*
    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {

            if(player.getProperty() != null || Property.getClosest(player.getLocation(), 10f) != null) {
                System.out.println("PropertyCommands :: beforeChcek. Cmd " + cmd + " returning true");
                return true;
            }
        }
        return false;
    }
*/


    @Command
    @CommandHelp("Nuperka versla")
    public boolean buyBiz(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.getClosest(player.getLocation(), 5f);
        if(business == null || business.getOwner() != LtrpPlayer.INVALID_USER_ID) {
            player.sendErrorMessage("Prie jûsø nëra jokio verslo arba jis neparduodamas");
        } else if(business.getPrice() > player.getMoney())
            player.sendErrorMessage("Jums neuþtenka pinigø ásigyti ðá verslà");
        else {
            int price = business.getPrice();
            business.setOwner(player.getUUID());
            player.giveMoney(-price);
            LtrpWorld.get().addMoney(price);
            player.sendMessage("Sëkmingai ásigijote verslà uþ " + Currency.SYMBOL + price + ".");
            eventManager.dispatchEvent(new BusinessBuyEvent(business, player));
        }
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia pasiûlymà pirkti jûsø verslà kitam þaidëjui")
    public boolean sellBiz(Player p, @CommandParameter(name = "ÞaidëjoID/Dalis vardo")LtrpPlayer target,
                           @CommandParameter(name = "Verslo kaina")int price) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Business business = Business.getClosest(player.getLocation(), 6f);
        if(target == null) {
            return false;
        } else if(business == null || !business.isOwner(player))
            player.sendErrorMessage("Jûs nestovite prie verslo arba jis jums nepriklauso!");
        else if(player.getDistanceToPlayer(target) > 10f)
            player.sendErrorMessage("Þaidëjas yra per toli!");
        else if(price < 0)
            player.sendErrorMessage("Kaina negali bûti neigiama!");
        else if(player.getIp().equals(target.getIp()))
            player.sendErrorMessage("Negalite parduoti verslo savo vartotojui.");
        else if(target.containsOffer(BuyBusinessOffer.class))
            player.sendErrorMessage("Ðiam þaidëjui jau kaþkas siûlo pirkti verslà, palaukite.");
        else {
            BuyBusinessOffer offer = new BuyBusinessOffer(target, player, eventManager, business, price);
            target.getOffers().add(offer);
            player.sendMessage(Color.BUSINESS, "Pasiûlymas pirkti jûsø verslà \"" + business.getName() + "\" uþ " + price + Currency.SYMBOL + " " + target.getName() + " iðsiøstas");
            target.sendMessage(Color.BUSINESS, "Þaidëjas " + player.getName() + " siûlo jums pirkti jo verslà uþ " + price + Currency.SYMBOL + ". Raðykite /accept business norëdami já pirkti.");
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia raðyti þinutes bûnant prie durø, kurias matys viduje esantys þaidëjai")
    public boolean ds(Player pp, @CommandParameter(name = "Tekstas")String text) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        Property property = Property.getClosest(player.getLocation(), 5f);
        if(property != null) {
            LtrpPlayer.get()
                    .stream()
                    .filter(p -> Property.get(p).equals(property) || Property.getClosest(p.getLocation(), 15f) != null)
                    .forEach(p -> {
                        String inMsg = String.format("%s ðaukia á duris: %s", player.getCharName(), text);
                        String outMsg = String.format("%s ðaukia pro duris: %s", player.getCharName(), text);
                        if (Property.get(p) != null)
                            p.sendMessage(Color.WHITE, inMsg);
                        else
                            p.sendMessage(Color.WHITE, outMsg);
                    });
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia pasibelsti á namo/verslo/garaþo duris")
    public boolean knock(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        Property property = Property.getClosest(player.getLocation(), 5f);
        if(property != null) {
            LtrpPlayer.get()
                    .stream()
                    .filter(p -> Property.get(p).equals(property) || Property.getClosest(p.getLocation(), 15f) != null)
                    .forEach(p -> {
                        if(Property.get(p).equals(property))
                            property.sendActionMessage("Kaþkas beldþiasi á duris");
                        else
                            player.sendActionMessage("pasibeldþia á duris");
                    });
        }
        return true;
    }

    // TODO cmd:furniture
}
