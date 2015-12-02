package lt.ltrp;


import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.DAOFactory;
import lt.ltrp.item.ItemController;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerController;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.server.GameModeInitEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.resource.Gamemode;
import net.gtaun.util.event.EventManager;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LtrpGamemode extends Gamemode {

    public static final String Version = "1.0";
    public static final String BuildDate = "2015.11.12";
    private static final DAOFactory dao = DAOFactory.getInstance();

    @Override
    protected void onEnable() throws Throwable {
        EventManager eventManager = getEventManager();
        ItemController.init();

        new PlayerController(eventManager);
        new PawnCallbacks(eventManager);

        System.out.println("About to do it");
        AmxCallable createDynamicObject = null;
        for(AmxInstance instance : Shoebill.get().getAmxInstanceManager().getAmxInstances()) {
            createDynamicObject = instance.getPublic("CreateDynamicObject");
            if(createDynamicObject != null) {
                //found CreateDynamicObject native, call it like this:
                createDynamicObject.call(18421, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f); //normal pawn arguments. Make sure you put a f after a Float value, like this: 13.0f or 0f
                break;
            }
        }
        System.out.println("Okay");

        Connection con = dao.getConnection();
        Statement stmt = con.createStatement();
        ResultSet result = stmt.executeQuery("SHOW TABLES");
        stmt.close();
        con.close();
        //for(float f : values) {
        //    System.out.println(f);
       // }
        //getPos.call("default_spawn", x, y, z);


        eventManager.registerHandler(GameModeInitEvent.class, e-> {
            System.out.println("\n\n\n\n\n\n\n\n");
            float x = 0.0f, y = 0.0f, z = 0.0f;
            ReferenceFloat refX = new ReferenceFloat(x);
            ReferenceFloat refY = new ReferenceFloat(y);
            ReferenceFloat refZ = new ReferenceFloat(z);
            AmxCallable func = PawnFunc.getNativeMethod("AFunction");
            if(func != null) {
                func.call("string", refX, refY, refZ);
            }
            else System.out.println("AFunction is null");
            System.out.println("Result from AFunction:. " + refX.getValue() + " " + refY.getValue() + " " + refZ.getValue());

            float x2 = 0.0f, y2 = 0.0f, z2 = 0.0f;
            ReferenceFloat refX2 = new ReferenceFloat(x2);
            ReferenceFloat refY2 = new ReferenceFloat(y2);
            ReferenceFloat refZ2 = new ReferenceFloat(z2);
            AmxCallable func2 = PawnFunc.getNativeMethod("AFunction2");
            if(func2 != null) {
                func2.call(refX2, refY2, refZ2);
            }
            else System.out.println("AFunction2 is null");
            System.out.println("Result from AFunction2:. " + refX2.getValue() + " " + refY2.getValue() + " " + refZ2.getValue());
        });

    }

    @Override
    protected void onDisable() throws Throwable {

    }

    public static DAOFactory getDao() {
        return dao;
    }
}