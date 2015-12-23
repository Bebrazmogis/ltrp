package lt.ltrp.Util;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class PawnFunc {

    public static AmxCallable getNativeMethod(String name) {
        AmxCallable nativeMethod = null;
        for(AmxInstance instance : Shoebill.get().getAmxInstanceManager().getAmxInstances()) {
            if((nativeMethod = instance.getPublic(name)) != null)
                break;
        }
        return nativeMethod;
    }


    public static Vector3D Data_GetLocationVector(String key) {
        Vector3D vector = new Vector3D();
        ReferenceFloat refX = new ReferenceFloat(vector.getX());
        ReferenceFloat refY = new ReferenceFloat(vector.getY());
        ReferenceFloat refZ = new ReferenceFloat(vector.getZ());
        AmxCallable method = getNativeMethod("Data_GetCoordinates");
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
        AmxCallable method = getNativeMethod("Data_GetCoordinates");
        if(method != null) {
            method.call(key, refX, refY, refZ);
            location.set(refX.getValue(), refY.getValue(), refZ.getValue());
        }
        method = getNativeMethod("Data_GetInterior");
        if(method != null) {
            location.setInteriorId((Integer)method.call(key));
        }

        method = getNativeMethod("Data_GetVirtualWorld");
        if(method != null) {
            location.setWorldId((Integer)method.call(key));
        }
        return location;
    }

}
