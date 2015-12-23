package lt.ltrp;


import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.DAOFactory;
import lt.ltrp.dmv.DmvManager;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.item.ItemController;
import lt.ltrp.job.JobManager;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerController;
import lt.ltrp.property.PropertyManager;
import lt.ltrp.vehicle.VehicleManager;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.player.PlayerClickMapEvent;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.server.GameModeInitEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Server;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.resource.Gamemode;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LtrpGamemode extends Gamemode {

    public static final String Version = "1.0";
    public static final String BuildDate = "2015.12.03";
    private static DAOFactory dao;
    private static final Logger logger = LoggerFactory.getLogger(LtrpGamemode.class);
    private Timer paydayTimer;

    @Override
    protected void onEnable() throws Throwable {

        getEventManager().registerHandler(PlayerClickMapEvent.class, e -> {
           LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null && player.getAdminLevel() > 0) {
                player.setLocation(e.getPosition());
            }
        });

        schedulePaydayTimer();
        logger.debug("DATA DIR: " + Shoebill.get().getResourceManager().getGamemode().getDataDir().getAbsolutePath());
        EventManager eventManager = getEventManager();
        ItemController.init();
        JobManager jobManager = JobManager.getInstance();
        VehicleManager vehicleManager = VehicleManager.get();
        PropertyManager propertyManager = PropertyManager.get();
        DmvManager dmvManager = DmvManager.getInstance();

        new PlayerController(eventManager);
        new PawnCallbacks(eventManager);

        System.out.println("About to do it");
        AmxCallable createDynamicObject = null;
        for(AmxInstance instance : Shoebill.get().getAmxInstanceManager().getAmxInstances()) {
            createDynamicObject = instance.getPublic("CreateDynamicObject");
            if(createDynamicObject != null) {
                //found CreateDynamicObject native, call it like this:
                createDynamicObject.call(18421, 0.0f, 0.0f, 10.0f, 0.0f, 0.0f, 0.0f); //normal pawn arguments. Make sure you put a f after a Float value, like this: 13.0f or 0f
                break;
            }
        }
        System.out.println("Okay");
    }

    private void schedulePaydayTimer() {
        LocalDateTime now = LocalDateTime.now();
        int seconds = (60 - now.getMinute()) * 60;
        seconds += 60 - now.getSecond();
        logger.info("Scheduling payday timer in " + seconds + " seconds");
        paydayTimer = Timer.create(seconds * 1000, ticks -> {
            logger.info("Sending PayDay event. Current hour is " + LocalDateTime.now().getHour() + ". ");
            getEventManager().dispatchEvent(new PaydayEvent(LocalDateTime.now().getHour()));
            schedulePaydayTimer();
        });
    }

    @Override
    protected void onDisable() throws Throwable {

    }

    public static DAOFactory getDao() {
        if(dao == null) {
            dao = DAOFactory.getInstance();
        }
        return dao;
    }
}