package lt.ltrp.item;

import lt.ltrp.item.constant.ItemType;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.14.
 */
public class ToolboxItem extends BasicItem {

    public ToolboxItem(EventManager eventManager) {
        this(0, "Árankiø deþutë", eventManager);
    }

    public ToolboxItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.Toolbox, false);
    }

    /*
    @ItemUsageOption(name = "Naudoti")
    public boolean use(LtrpPlayer player) {
        // TODO its implemented in vehiclethief module, needs testing
        return false;
    }
    */

}
