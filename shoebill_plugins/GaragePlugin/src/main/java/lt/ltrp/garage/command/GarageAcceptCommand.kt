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
}