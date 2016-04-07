package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class MaskItem extends ClothingItem {

    public static final int MIN_LEVEL = 2;

    public MaskItem(String name, EventManager eventManager, int modelid) {
        this(0, name, eventManager, modelid);
    }

    public MaskItem(int id, String name, EventManager eventManager, int modelid) {
        super(id, name, eventManager, ItemType.Mask, modelid, PlayerAttachBone.HEAD);
    }

    @Override
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getLevel() < MIN_LEVEL) {
            player.sendErrorMessage("Jums reikia " + MIN_LEVEL + " lygio kad gal�tum�te naudoti kauk�.");
        } else if(!LtrpPlayer.get().stream()
                .filter(p -> p.isAdmin() && p.getAdminLevel() > 0)
                .findFirst()
                .isPresent()) {
            player.sendErrorMessage("Kauk� galima naudoti tik tada kai yra prisijungusi� administratori�.");
        } else {
            if(super.equip(player, inventory)) {
                player.sendActionMessage("i�sitraukia ir ant galvos u�simaun� veido kauk�");
                player.setMasked(true);
                LtrpPlayer.sendAdminMessage("�aid�jas " + player.getName() + " u�sid�jo kauk�. Jo kauk�s vardas: " + player.getMaskName());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(super.unequip(player, inventory)) {
            player.setMasked(false);
            player.sendActionMessage("nusimauna veido kauk� sau nuo veido");
            return true;
        } else {
            return false;
        }
    }

}
