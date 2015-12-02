package lt.ltrp.item;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class DrugItem extends ConsumableItem {

    public DrugItem(String name, int id, ItemType type, int dosesLeft) {
        super(name, id, type, dosesLeft, true);
    }


}
