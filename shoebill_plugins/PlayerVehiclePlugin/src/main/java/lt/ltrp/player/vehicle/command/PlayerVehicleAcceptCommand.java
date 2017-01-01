package lt.ltrp.player.vehicle.command;

import lt.ltrp.player.vehicle.PlayerVehiclePlugin;
import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission;
import lt.ltrp.player.vehicle.data.BuyVehicleOffer;
import lt.ltrp.data.Color;
import lt.ltrp.player.vehicle.event.PlayerVehicleSellEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class PlayerVehicleAcceptCommand {

    private EventManager eventManager;

    public PlayerVehicleAcceptCommand(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Command
    public boolean car(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerVehiclePlugin plugin = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class);
        BuyVehicleOffer offer = player.getOffer(BuyVehicleOffer.class);
        if(offer == null) {
            player.sendErrorMessage("Jums niekas nesiûlo pirkti automobilio!");
        } else if(offer.getVehicle().getOwnerId() != offer.getOfferedBy().getUUID()) {
            player.sendErrorMessage("Automobilis jau parduotas.");
        } else if(plugin.getPlayerOwnedVehicleCount(player) >= plugin.getMaxOwnedVehicles(player)) {
            player.sendErrorMessage("Daugiau transporto priemoniø turëti negalite.");
        } else if(player.getMoney() < offer.getPrice()) {
            player.sendErrorMessage("Jums neuþtenka pinigø. Siûlomos transporto priemonës kaina " + lt.ltrp.constant.Currency.SYMBOL + offer.getPrice());
        } else if(player.getDistanceToPlayer(offer.getOfferedBy()) > 5f) {
            player.sendErrorMessage(offer.getOfferedBy().getCharName() + " yra per toli!");
        } else {
            int price = offer.getPrice();
            LtrpPlayer offerer = offer.getOfferedBy();
            PlayerVehicle vehicle = offer.getVehicle();
            player.sendMessage(Color.NEWS, "Nusipirkote " + vehicle.getName() + " uþ " + lt.ltrp.constant.Currency.SYMBOL + price + " ið " + offerer.getCharName());
            offerer.sendMessage(Color.NEWS, "Pardavëte " + vehicle.getName() + " uþ " + lt.ltrp.constant.Currency.SYMBOL + price + " " + player.getCharName());

            // An owner changes all the permissions he had given anyone must be removed
            // But the new owner must have all permissions as well as actual ownership
            vehicle.getPermissions().keySet().forEach(vehicle::removePermissions);
            plugin.getVehiclePermissionDao().remove(vehicle);

            for(PlayerVehiclePermission perm : PlayerVehiclePermission.values()) {
                vehicle.addPermission(player, perm);
                plugin.getVehiclePermissionDao().add(vehicle, player, perm);
            }
            plugin.getVehicleDao().setOwner(vehicle, player);
            eventManager.dispatchEvent(new PlayerVehicleSellEvent(offerer, vehicle, player, price));
        }
        player.getOffers().remove(offer);
        return true;
    }

}
