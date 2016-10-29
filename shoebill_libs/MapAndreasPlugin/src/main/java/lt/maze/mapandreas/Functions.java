package lt.maze.mapandreas;

import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReturnType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.04.20.
 */
class Functions {


    private static final Map<String, AmxCallable> functions = new HashMap<>();

    static void registerFunctions(AmxInstance instance) {
        functions.put("MapAndreas_Init", instance.getNative("MapAndreas_Init", ReturnType.INTEGER));
        functions.put("MapAndreas_Unload", instance.getNative("MapAndreas_Unload", ReturnType.INTEGER));
        functions.put("MapAndreas_FindZ_For2DCoord", instance.getNative("MapAndreas_FindZ_For2DCoord", ReturnType.FLOAT));
        functions.put("MapAndreas_FindAverageZ", instance.getNative("MapAndreas_FindAverageZ", ReturnType.FLOAT));
        functions.put("MapAndreas_SetZ_For2DCoord", instance.getNative("MapAndreas_SetZ_For2DCoord", ReturnType.INTEGER));
        functions.put("MapAndreas_SaveCurrentHMap", instance.getNative("MapAndreas_SaveCurrentHMap", ReturnType.INTEGER));
        functions.put("MapAndreas_GetAddress", instance.getNative("MapAndreas_GetAddress", ReturnType.INTEGER));
    }

    static void unregisterFunctions(AmxInstance instance) {
        functions.keySet().forEach(instance::unregisterFunction);
    }


    static void MapAndreas_Init(int mode, String name) {
        functions.get("MapAndreas_Init").call(mode, name, name.length());
    }
    static void MapAndreas_Unload() {
        functions.get("MapAndreas_Unload").call();
    }

    static float MapAndreas_FindZ_For2DCoord(float x, float y) {
        return (Float)functions.get("MapAndreas_FindZ_For2DCoord").call(x, y);
    }

    static float MapAndreas_FindAverageZ(float x, float y) {
        return (Float)functions.get("MapAndreas_FindAverageZ").call(x, y);
    }

    static void MapAndreas_SetZ_For2DCoord(float x, float y, float z) {
        functions.get("MapAndreas_SetZ_For2DCoord").call(x,  y, z);
    }

    static void MapAndreas_SaveCurrentHMap(String name) {
        functions.get("MapAndreas_SaveCurrentHMap").call(name);
    }

    static int MapAndreas_GetAddress() {
        return (Integer)functions.get("MapAndreas_GetAddress").call();
    }

}
