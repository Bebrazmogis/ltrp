package lt.maze.mapandreas;


import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.data.Vector2D;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class MapAndreas extends Plugin {

    private static final Logger logger = LoggerFactory.getLogger(MapAndreas.class);


    // native MapAndreas_Init(mode, name[]="", len=sizeof(name));
    public static void init(MapAndreasMode mode, String name) {
        Functions.MapAndreas_Init(mode.getValue(), name);
    }

    public static void init(MapAndreasMode mode) {
        init(mode, "");
    }


    // native MapAndreas_Unload();
    public static void unload() {
        Functions.MapAndreas_Unload();
    }


    // native MapAndreas_FindZ_For2DCoord(Float:X, Float:Y, &Float:Z);
    public static float findZ(float x, float y) {
        return Functions.MapAndreas_FindZ_For2DCoord(x, y);
    }

    public static float findZ(Vector2D location) {
        return findZ(location.x, location.y);
    }

    // native MapAndreas_FindAverageZ(Float:X, Float:Y, &Float:Z);
    public static float findAverageZ(float x, float y) {
        return Functions.MapAndreas_FindAverageZ(x, y);
    }

    // native MapAndreas_SetZ_For2DCoord(Float:X, Float:Y, Float:Z);
    public static void setZ(float x, float y, float z) {
        Functions.MapAndreas_SetZ_For2DCoord(x, y, z);
    }

    // native MapAndreas_SaveCurrentHMap(name[]);
    public static void saveCurrentMap(String name) {
        Functions.MapAndreas_SaveCurrentHMap(name);
    }

    // native MapAndreas_GetAddress(); //only for plugins
    public static int getAddress() {
        return Functions.MapAndreas_GetAddress();
    }


    @Override
    protected void onEnable() throws Throwable {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("MapAndreas registering functions..");
        Functions.registerFunctions(AmxInstance.getDefault());
        logger.debug("MapAndreas initializing");
        init(MapAndreasMode.Full);
        logger.debug("MapAndreas loaded");
    }

    @Override
    protected void onDisable() throws Throwable {
        Functions.unregisterFunctions(AmxInstance.getDefault());
    }
}
