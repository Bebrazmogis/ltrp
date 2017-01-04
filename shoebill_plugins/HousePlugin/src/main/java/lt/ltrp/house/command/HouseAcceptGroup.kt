package lt.ltrp.house.command

/**
 * @author Bebras
 * 2016.12.30.
 */
class HouseAcceptGroup {

    @Command
    public boolean house(Player p) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
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