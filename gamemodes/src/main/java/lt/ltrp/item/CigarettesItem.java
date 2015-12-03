package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.SpecialAction;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class CigarettesItem extends DurableItem {

    private static final int MAX_CIGARETTES = 20;

    public CigarettesItem(String name, int id, int durabilityy) {
        super(name, id, ItemType.Cigarettes, durabilityy, MAX_CIGARETTES, false);
    }


    @ItemUsageOption(name = "U�sir�kyti")
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().containsType((ItemType.Lighter)) || inventory.containsType(ItemType.Lighter)) {
            player.setSpecialAction(SpecialAction.SMOKE_CIGGY);
            this.use();
            player.sendActionMessage("i��straukia cigaret�, ja prisidega ir pradeda r�kyti");
        }
        return false;
    }



}
