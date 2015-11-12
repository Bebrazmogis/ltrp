package lt.ltrp.Util;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class PawnFunc {

    public static AmxCallable getNativeMethod(String name) {
        AmxCallable nativeMethod = null;
        for(AmxInstance instance : Shoebill.get().getAmxInstanceManager().getAmxInstances()) {
            if((nativeMethod = instance.getNative(name)) != null)
                break;
        }
        return nativeMethod;
    }

}
