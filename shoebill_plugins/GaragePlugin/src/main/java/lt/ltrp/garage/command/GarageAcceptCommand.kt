package lt.ltrp.garage.command

/**
 * @author Bebras
 * 2016.12.30.
 */
class GarageAcceptCommand {
     @Command
    public boolean garage(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
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
}