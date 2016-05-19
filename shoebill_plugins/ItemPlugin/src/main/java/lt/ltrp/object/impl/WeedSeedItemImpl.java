package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.data.Color;
import lt.ltrp.data.HouseWeedSapling;
import lt.ltrp.event.property.PlayerPlantWeedEvent;
import lt.ltrp.object.*;
import lt.ltrp.util.ItemUsageOption;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class WeedSeedItemImpl extends BasicItem implements WeedSeedItem {



    public WeedSeedItemImpl(EventManager eventManager) {
        this(0, "�ol�s s�klos", eventManager);
    }

    public WeedSeedItemImpl(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.WeedSeed, true);
    }



    @ItemUsageOption(name = "Sodinti")
    public boolean plant(LtrpPlayer player, Inventory inventory) {
        House house = House.get(player);
        if(house != null) {
            if(house.getOwner() == player.getUUID()) {
                Location location = player.getLocation();
                location.setZ(location.getZ()-1.1f);
                HouseWeedSapling sapling = new HouseWeedSapling(location, house, player.getUUID(), getEventManager());
                sapling.startGrowth();
                house.getWeedSaplings().add(sapling);
                getEventManager().dispatchEvent(new PlayerPlantWeedEvent(player, house, sapling));
                player.sendMessage(Color.NEWS, "Jums s�kmingai pavyko pas�ti �ol�s s�klas, dabar beliek� laukti kol augalas pilnai u�augs.");
            } else {
                player.sendErrorMessage("Tai ne j�s� namas!");
            }
        } else {
            player.sendErrorMessage("J�s neesate namuose.");
        }
        return false;
    }
}
