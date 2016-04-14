package lt.ltrp.item.object;

import lt.ltrp.api.NamedEntity;
import lt.ltrp.item.constant.ItemType;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;

/**
 * @author Bebras
 *         2015.11.14.
 */
public interface Item extends NamedEntity, Destroyable {

    public ItemType getType();
    public boolean isStackable();
    public int getAmount();
    public void setAmount(int amount);
    void showOptions(Player player, Inventory inventory, AbstractDialog parentDialog);

    public static Item get(String className, Object[] params) throws Exception {
        Class<?> cls = Class.forName(className);
        if(cls != null) {
            for(Constructor<?> constr : cls.getConstructors()) {
                if(constr.getParameterCount() == params.length) {
                    int i = 0;
                    for(Parameter param : constr.getParameters()) {
                        if(param.getType() != params[i].getClass()) {
                            throw new InvalidParameterException(className + " " + i + " parameter must be of type " + param.getType().getName());
                        }
                        i++;
                    }
                    Item item = (Item) cls.newInstance();
                }
            }
        }
        return null;
    }

}
