package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.House;
import lt.ltrp.property.HouseWeedSapling;
import lt.ltrp.property.Property;
import lt.ltrp.property.event.PlayerPlantWeedEvent;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class WeedSeedItem extends BasicItem {



    public WeedSeedItem(EventManager eventManager) {
        this(0, "Þolës sëklos", eventManager);
    }

    public WeedSeedItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.WeedSeed, true);
    }



    @ItemUsageOption(name = "Sodinti")
    public boolean plant(LtrpPlayer player, Inventory inventory) {
        Property property = player.getProperty();
        if(property instanceof House) {
            House house = (House)property;
            if(house.getOwnerUserId() == player.getUUID()) {
                Location location = player.getLocation();
                location.setZ(location.getZ()-1.1f);
                HouseWeedSapling sapling = new HouseWeedSapling(location, house, player.getUUID(), getEventManager());
                sapling.startGrowth();
                house.getWeedSaplings().add(sapling);
                getEventManager().dispatchEvent(new PlayerPlantWeedEvent(player, house, sapling));
                player.sendMessage(Color.NEWS, "Jums sëkmingai pavyko pasëti þolës sëklas, dabar beliekà laukti kol augalas pilnai uþaugs.");
            } else {
                player.sendErrorMessage("Tai ne jûsø namas!");
            }
        } else {
            player.sendErrorMessage("Jûs neesate namuose.");
        }
        return false;
    }
}
