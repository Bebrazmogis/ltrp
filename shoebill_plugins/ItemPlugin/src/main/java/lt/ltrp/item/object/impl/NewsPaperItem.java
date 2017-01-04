package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.ItemUsageOption;
import net.gtaun.util.event.EventManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Timestamp;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class NewsPaperItem extends BasicItem {

    private Timestamp date;

    public NewsPaperItem(int id, String name, EventManager eventManager, Timestamp timestamp) {
        super(id, name, eventManager, ItemType.Newspaper, false);
        this.date = timestamp;
    }


    public Timestamp getDate() {
        return date;
    }


    @ItemUsageOption(name = "Skaityti")
    public boolean read(LtrpPlayer player) {
        throw new NotImplementedException();
    }
}
