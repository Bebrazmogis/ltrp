package lt.ltrp.util;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class PawnFunc {

    private static final Logger logger = LoggerFactory.getLogger(PawnFunc.class);

    public static AmxCallable getNativeMethod(String name) {
        AmxCallable nativeMethod = null;
        for(AmxInstance instance : Shoebill.get().getAmxInstanceManager().getAmxInstances()) {
            if((nativeMethod = instance.getNative(name)) != null)
                break;
        }
        return nativeMethod;
    }

    public static AmxCallable getPublicMethod(String name) {
        AmxCallable publicMethod = null;
        for(AmxInstance instance : Shoebill.get().getAmxInstanceManager().getAmxInstances()) {
            if((publicMethod = instance.getPublic(name)) != null)
                break;
        }
        return publicMethod;
    }


    public static Vector3D Data_GetLocationVector(String key) {
        Vector3D vector = new Vector3D();
        ReferenceFloat refX = new ReferenceFloat(vector.getX());
        ReferenceFloat refY = new ReferenceFloat(vector.getY());
        ReferenceFloat refZ = new ReferenceFloat(vector.getZ());
        AmxCallable method = getPublicMethod("Data_GetCoordinates");
        if(method != null) {
            method.call(key, refX, refY, refZ);
            vector.set(refX.getValue(), refY.getValue(), refZ.getValue());
        }
        return vector;
    }

    public static Location Data_GetLocation(String key) {
        Location location = new Location();
        ReferenceFloat refX = new ReferenceFloat(location.getX());
        ReferenceFloat refY = new ReferenceFloat(location.getY());
        ReferenceFloat refZ = new ReferenceFloat(location.getZ());
        AmxCallable method = getPublicMethod("Data_GetCoordinates");
        if(method != null) {
            method.call(key, refX, refY, refZ);
            location.set(refX.getValue(), refY.getValue(), refZ.getValue());
        }
        method = getPublicMethod("Data_GetInterior");
        if(method != null) {
            location.setInteriorId((Integer)method.call(key));
        }

        method = getPublicMethod("Data_GetVirtualWorld");
        if(method != null) {
            location.setWorldId((Integer)method.call(key));
        }
        return location;
    }

    public static boolean isPlayerInRangeOfCoords(Player player, float distance, String key) {
        AmxCallable func = getPublicMethod("Data_IsPlayerInRangeOfCoords");
        if(func != null) {
            return (boolean)func.call(player.getId(), distance, key);
        } else {
            logger.error("Data_IsPlayerInRangeOfCoords is not registered");
        }
        return false;
    }

}
