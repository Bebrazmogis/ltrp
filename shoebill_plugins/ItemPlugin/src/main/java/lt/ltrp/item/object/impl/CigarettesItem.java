package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.util.ItemUsageOption;import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class CigarettesItem extends DurableItemImpl {

    private static final int MAX_CIGARETTES = 20;

    private HandlerEntry keyStateEventEntry;
    private Timer smokingTimer;
    private Timer animDelayTimer;

    public CigarettesItem(int id, String name, EventManager eventManager, int durabilityy) {
        super(id, name, eventManager, ItemType.Cigarettes, durabilityy, MAX_CIGARETTES, false);

        keyStateEventEntry = getEventManager().registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(!player.isInAnyVehicle() && !e.getOldState().isKeyPressed(PlayerKey.FIRE) && player.getKeyState().isKeyPressed(PlayerKey.FIRE)) {
                player.applyAnimation("SMOKING", "M_SMK_DRAG", 4.1f, false, true, true, false, 0, false);
                animDelayTimer = Timer.create(2200, 1, i -> {
                    player.applyAnimation("SMOKING", "M_SMK_TAP", 4.1f, false, true, true, false, 0, false);
                });
                animDelayTimer.start();
            }
        });
    }

    public CigarettesItem(EventManager eventManager) {
        this(0, "Cigaretës", eventManager, MAX_CIGARETTES);
    }

    @ItemUsageOption(name = "Uþsirûkyti")
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().containsType((ItemType.Lighter)) || inventory.containsType(ItemType.Lighter)) {
            if(player.getSpecialAction() != SpecialAction.SMOKE_CIGGY) {
                player.setSpecialAction(SpecialAction.SMOKE_CIGGY);
                super.use();
                player.sendActionMessage("iðástraukia cigaretæ, ja prisidega ir pradeda rûkyti");
                player.applyAnimation("SMOKING", "M_smk_in", 4.1f, false, false, false, false, 0, false);
                smokingTimer = Timer.create(60000, 1, i -> {
                    player.sendActionMessage("numeta nuorukà ant þemës ir uþtrypia jà koja");
                    keyStateEventEntry.cancel();
                    keyStateEventEntry = null;
                    animDelayTimer = null;
                });
                smokingTimer.start();
            }
        }
        return false;
    }


    @Override
    public void destroy() {
        if(keyStateEventEntry != null)
            keyStateEventEntry.cancel();
        if(smokingTimer != null)
            smokingTimer.destroy();
        if(animDelayTimer != null)
            animDelayTimer.destroy();
        super.destroy();
    }
}
