package lt.maze.mapandreas;


import net.gtaun.shoebill.ShoebillMain;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.data.Vector2D;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;

/**
 * @author Bebras
 *         2015.12.03.
 */
@ShoebillMain(name = "Map Andreas Wrapper plugin", author = "Bebras",
        description = "A wrapper of SAMP MapAndreas plugin\n" +
                "http://forum.sa-mp.com/showthread.php?t=275492\n" +
                "I merely created the wrapper, all credits for the actual C++ plugin go to Mauzen")
public class MapAndreas extends Plugin {

    // native MapAndreas_Init(mode, name[]="", len=sizeof(name));
    public void init(MapAndreasMode mode, String name) {
        Functions.MapAndreas_Init(mode.getValue(), name);
    }

    public void init(MapAndreasMode mode) {
        init(mode, "");
    }


    // native MapAndreas_Unload();
    public void unload() {
        Functions.MapAndreas_Unload();
    }


    // native MapAndreas_FindZ_For2DCoord(Float:X, Float:Y, &Float:Z);
    public float findZ(float x, float y) {
        return Functions.MapAndreas_FindZ_For2DCoord(x, y);
    }

    public float findZ(Vector2D location) {
        return findZ(location.x, location.y);
    }

    // native MapAndreas_FindAverageZ(Float:X, Float:Y, &Float:Z);
    public float findAverageZ(float x, float y) {
        return Functions.MapAndreas_FindAverageZ(x, y);
    }

    // native MapAndreas_SetZ_For2DCoord(Float:X, Float:Y, Float:Z);
    public void setZ(float x, float y, float z) {
        Functions.MapAndreas_SetZ_For2DCoord(x, y, z);
    }

    // native MapAndreas_SaveCurrentHMap(name[]);
    public void saveCurrentMap(String name) {
        Functions.MapAndreas_SaveCurrentHMap(name);
    }

    // native MapAndreas_GetAddress(); //only for plugins
    public int getAddress() {
        return Functions.MapAndreas_GetAddress();
    }


    @Override
    protected void onEnable() throws Throwable {
        Logger logger = getLogger();
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
