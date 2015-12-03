package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.House;
import lt.ltrp.property.HouseUpgradeType;
import lt.ltrp.property.Property;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class HouseAudioItem extends BasicItem {

    public HouseAudioItem(String name, int id, ItemType type) {
        super(name, id, type, false);
    }


    @ItemUsageOption(name = "Ádiegti á namus")
    public boolean installToHouse(LtrpPlayer player) {
        Property property = player.getProperty();
        if(property != null && property instanceof House) {
            House house = (House)property;
            if(!house.isUpgradeInstalled(HouseUpgradeType.Radio)) {
                MsgboxDialog.create(player, ItemController.getEventManager())
                        .caption("Patvirtinimas")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .message("Ðio veiksmo atstatyti neámanoma."
                                + "\nAr tikrai norite tæsti?"
                                + (house.getOwnerUserId() != player.getUserId() ? "\n\n{FF0000}Pastaba. Ðis namas jums nepriklauso." : ""))
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
