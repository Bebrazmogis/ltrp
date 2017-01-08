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