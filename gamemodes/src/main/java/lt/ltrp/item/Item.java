package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.object.Destroyable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;

/**
 * @author Bebras
 *         2015.11.14.
 */
public interface Item extends Destroyable {

    // Getter, setters and such
    public int getId();
    public void setId(int id);
    public String getName();
    public void setName(String name);
    public ItemType getType();
    public boolean isStackable();
    public int getAmount();
    public void setAmount(int amount);

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
