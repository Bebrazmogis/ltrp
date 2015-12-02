package lt.ltrp.item;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.House;
import lt.ltrp.property.Property;
import net.gtaun.shoebill.amx.AmxCallable;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class WeedSeedItem extends BasicItem {



    public WeedSeedItem(String name, int id, ItemType type) {
        super(name, id, type, true);
    }



    @ItemUsageOption(name = "Sodinti")
    public boolean plant(LtrpPlayer player, Inventory inventory) {
        Property property = player.getProperty();
        if(property instanceof House) {
            House house = (House)property;
            if(house.getOwnerUserId() == player.getUserId()) {
                AmxCallable func = PawnFunc.getNativeMethod("GetHouseFreeWeedSlotCount");
                AmxCallable indexFunc = PawnFunc.getNativeMethod("GetHouseIndex");
                if(func != null && indexFunc != null) {
                    int index = (Integer)indexFunc.call(house.getUid());
                    if(index != -1) {
                        int freeSlotCount = (Integer)func.call(index);
                        if(freeSlotCount > 0) {
                            func = PawnFunc.getNativeMethod("AddHouseWeedSapling");
                            if(func != null) {
                                func.call(player.getId(), index);
                                player.sendMessage(Color.NEWS, "Jums sëkmingai pavyko pasëti þolës sëklas, dabar beliekà laukti kol augalas pilnai uþaugs.");
                            }
                        } else {
                            player.sendErrorMessage("Jûsø name nebëra vietos narkotikams auginti.");
                        }
                    }
                }

            } else {
                player.sendErrorMessage("Tai ne jûsø namas!");
            }
        } else {
            player.sendErrorMessage("Jûs neesate namuose.");
        }
        return false;
    }
}
