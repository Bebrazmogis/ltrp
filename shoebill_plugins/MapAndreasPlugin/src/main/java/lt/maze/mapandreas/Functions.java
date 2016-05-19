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
    }

}
