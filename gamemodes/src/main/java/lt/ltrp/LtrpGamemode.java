package lt.ltrp;


import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.DAOFactory;
import lt.ltrp.player.PlayerController;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.resource.Gamemode;
import net.gtaun.util.event.EventManager;

public class LtrpGamemode extends Gamemode {

    public static final String Version = "1.0";
    public static final String BuildDate = "2015.11.12";
    private static final DAOFactory dao = DAOFactory.getInstance();

    @Override
    protected void onEnable() throws Throwable {
        EventManager eventManager = getEventManager();

        new PlayerController(eventManager);
        new PawnCallbacks(eventManager);


        float x= 0.0f, y = 0.0f, z = 0.0f;
        AmxCallable getPos = PawnFunc.getNativeMethod("CreateDynamicObject");
        System.out.println("NULL?" + (getPos == null));
        int value  =(Integer)getPos.call(1224, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        //for(float f : values) {
        //    System.out.println(f);
       // }
        //getPos.call("default_spawn", x, y, z);
    }

    @Override
    protected void onDisable() throws Throwable {

    }

    public static DAOFactory getDao() {
        return dao;
    }
}