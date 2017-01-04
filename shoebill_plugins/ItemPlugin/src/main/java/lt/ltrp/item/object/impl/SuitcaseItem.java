package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class SuitcaseItem extends ClothingItemImpl {

    public SuitcaseItem(int id, String name, EventManager eventManager, int modelid) {
        super(id, name, eventManager, ItemType.Suitcase, modelid, PlayerAttachBone.HAND_RIGHT);
    }



}
