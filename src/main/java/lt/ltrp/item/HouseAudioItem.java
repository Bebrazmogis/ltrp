package lt.ltrp.item;

import lt.ltrp.common.data.Color;
import lt.ltrp.item.constant.ItemType;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.object.House;
import lt.ltrp.object.Property;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;



/**
 * @author Bebras
 *         2015.12.03.
 */
public class HouseAudioItem extends BasicItem {

    public HouseAudioItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.HouseAudio, false);
    }

    public HouseAudioItem(EventManager eventManager) {
        this(0, "Namø audio sistema", eventManager);
    }


    @ItemUsageOption(name = "Ádiegti á namus")
    public boolean installToHouse(LtrpPlayer player) {
        Property property = player.getProperty();
        if(property != null && property instanceof House) {
            House house = (House)property;
            if(!house.isUpgradeInstalled(HouseUpgradeType.Radio)) {
                MsgboxDialog.create(player, getEventManager())
                        .caption("Patvirtinimas")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .message("Ðio veiksmo atstatyti neámanoma - atgauti audio sistemos nebegalësite."
                                + "\nAr tikrai norite tæsti?"
                                + (house.getOwnerUserId() != player.getUUID() ? "\n\n{FF0000}Pastaba. Ðis namas jums nepriklauso." : ""))
                        .onClickOk(dialog -> {
                            house.addUpgrade(HouseUpgradeType.Radio);
                            player.sendMessage(Color.NEWS, "Sveikiname sëkmingai instaliavus garso sistemà. Gero klausymosi!");
                        })
                        .build()
                        .show();
            } else
                player.sendErrorMessage("Ðiame name jau yra audio aparatûra.");
        } else
            player.sendErrorMessage("Jûs neesate namuose");
        return false;
    }

}
