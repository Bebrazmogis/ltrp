package lt.ltrp.item;

import lt.ltrp.item.constant.ItemType;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class SuitcaseItem extends ClothingItem {

    public SuitcaseItem(int id, String name, EventManager eventManager, int modelid) {
        super(id, name, eventManager, ItemType.Suitcase, modelid, PlayerAttachBone.HAND_RIGHT);
    }



}
