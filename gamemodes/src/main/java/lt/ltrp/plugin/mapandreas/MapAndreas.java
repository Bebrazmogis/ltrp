package lt.ltrp.plugin.mapandreas;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.plugin.PluginWrapper;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class MapAndreas extends PluginWrapper {


    // native MapAndreas_Init(mode, name[]="", len=sizeof(name));
    public static void Init(MapAndreasMode mode, String name) {
        AmxCallable MapAndreas_Init = PawnFunc.getNativeMethod("MapAndreas_Init");
        if(MapAndreas_Init != null) {
            MapAndreas_Init.call(mode.getValue(), name, name.length());
        }
    }

    public static void Init(MapAndreasMode mode) {
        Init(mode, "");
    }


    // native MapAndreas_Unload();
    public static void Unload() {
        AmxCallable MapAndreas_Unload = PawnFunc.getNativeMethod("MapAndreas_Unload");
        if(MapAndreas_Unload != null) {
            MapAndreas_Unload.call();
        }
    }


    // native MapAndreas_FindZ_For2DCoord(Float:X, Float:Y, &Float:Z);
    public static float FindZ(float x, float y) {
        AmxCallable MapAndreas_FindZ_For2DCoord = PawnFunc.getNativeMethod("MapAndreas_FindZ_For2DCoord");
        float z = 0.0f;
        if(MapAndreas_FindZ_For2DCoord != null) {
            ReferenceFloat refZ = new ReferenceFloat(z);
            MapAndreas_FindZ_For2DCoord.call(x, y, refZ);
            z = refZ.getValue();
        }
        return z;
    }

    // native MapAndreas_FindAverageZ(Float:X, Float:Y, &Float:Z);
    public static float FindAverageZ(float x, float y) {
        AmxCallable MapAndreas_FindAverageZ = PawnFunc.getNativeMethod("MapAndreas_FindAverageZ");
        float z = 0.0f;
        if(MapAndreas_FindAverageZ != null) {
            ReferenceFloat refZ = new ReferenceFloat(z);
            MapAndreas_FindAverageZ.call(x, y, refZ);
            z = refZ.getValue();
        }
        return z;
    }

    // native MapAndreas_SetZ_For2DCoord(Float:X, Float:Y, Float:Z);
    public static void SetZ(float x, float y, float z) {
        AmxCallable MapAndreas_SetZ_For2DCoord = PawnFunc.getNativeMethod("MapAndreas_SetZ_For2DCoord");
        if(MapAndreas_SetZ_For2DCoord != null) {
            MapAndreas_SetZ_For2DCoord.call(x, y, z);
        }
    }

    // native MapAndreas_SaveCurrentHMap(name[]);
    public static void SaveCurrentMap(String name) {
        AmxCallable MapAndreas_SaveCurrentHMap = PawnFunc.getNativeMethod("MapAndreas_SaveCurrentHMap");
        if(MapAndreas_SaveCurrentHMap != null) {
            MapAndreas_SaveCurrentHMap.call(name);
        }
    }

    // native MapAndreas_GetAddress(); //only for plugins
    public static int GetAddress() {
        AmxCallable MapAndreas_GetAddress = PawnFunc.getNativeMethod("MapAndreas_GetAddress");
        if(MapAndreas_GetAddress != null) {
            return (Integer)MapAndreas_GetAddress.call();
        }
        return 0;
    }



}
