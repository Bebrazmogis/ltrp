package lt.maze.ysf;

import lt.maze.ysf.object.YSFObjectManager;
import lt.maze.ysf.object.YSFPlayer;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.amx.types.ReferenceString;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFPlugin extends Plugin {

    private static YSFPlugin instance;
    private static Logger logger;

    public static YSFPlugin getInstance() {
        return instance;
    }

    private PlayerLifecycleHolder playerLifecycleHolder;
    private YSFObjectManager ysfObjectManager;

    @Override
    public void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();
        if(logger == null)
            logger = LoggerFactory.getLogger(getClass());

        EventManager eventManager = getEventManager();
        this.playerLifecycleHolder = new PlayerLifecycleHolder(eventManager);
        this.playerLifecycleHolder.registerClass(YSFPlayer.class);

        this.ysfObjectManager = new YSFObjectManager(eventManager);

        Functions.registerFunctions(AmxInstance.getDefault());
        Callbacks.registerHandlers(Shoebill.get().getAmxInstanceManager(), eventManager);
        logger.info("YSF Wrapper plugin loaded. Tick rate " + getYSFTickRate());
    }

    @Override
    public void onDisable() throws Throwable {
        this.playerLifecycleHolder.destroy();
        this.ysfObjectManager.destroy();
    }

    public static YSFPlayer get(Player p) {
        return instance.playerLifecycleHolder.getObject(p, YSFPlayer.class);
    }


    public Logger getLogger() {
        return logger;
    }

    public static void setYSFTickRate(int ticks) {
        Functions.YSF_SetTickRate(ticks);
    }

    public static int getYSFTickRate() {
        return Functions.YSF_GetTickRate();
    }

    public static void setNightVisionFix(boolean enable) {
        Functions.YSF_EnableNightVisionFix(enable ? 1 : 0);
    }

    public static boolean isNightVisionFixEnabled() {
        return Functions.YSF_IsNightVisionFixEnabled() != 0;
    }

    public static boolean isBanned(String ip) {
        return Functions.IsBanned(ip) != 0;
    }

    public static void clearBanlist() {
        Functions.ClearBanList();
    }

    public static int getMTUSize() {
        return Functions.GetMTUSize();
    }

    public static String getLocalIp(int index) {
        ReferenceString string = new ReferenceString("", 128);
        Functions.GetLocalIP(index, string, string.getLength());
        return string.getValue();
    }

    public static int getColCount() {
        return Functions.GetColCount();
    }

    public static float getColSphereRadius(int modelId) {
        return Functions.GetColSphereRadius(modelId);
    }

    public static Vector3D getColSphereOffset(int modelId) {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        Functions.GetColSphereOffset(modelId, x, y, z);
        return new Vector3D(x.getValue(), y.getValue(), z.getValue());
    }
}
