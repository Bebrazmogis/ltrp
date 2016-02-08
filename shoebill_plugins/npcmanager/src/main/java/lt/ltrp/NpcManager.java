package lt.ltrp;

import net.gtaun.shoebill.SampObjectManager;
import net.gtaun.shoebill.SampObjectStore;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.object.Server;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.resource.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Bebras
 *         2016.02.07.
 */
public class NpcManager extends Plugin {

    private List<Npc> npcs = new ArrayList<>();
    private EventHandler handler;

    @Override
    protected void onEnable() throws Throwable {
        loadNpcs(new File(getDataDir(), "npcs"), getShoebill().getSampObjectManager());

        Server server = Server.get();
        for(Npc npc : npcs) {
            npc.connect(server);
        }
        handler = new EventHandler(getEventManager(), npcs);
    }

    @Override
    protected void onDisable() throws Throwable {
        handler.destroy();
        for(Npc npc : npcs) {
            if(npc.getPlayer() != null) {
                npc.getPlayer().kick();
            }
            if(npc.getVehicle() != null) {
                npc.getVehicle().destroy();
            }
        }
    }

    private  void loadNpcs(File npcDir, SampObjectManager objectManager) {
        File[] npcFiles = npcDir.listFiles();
        if(npcFiles != null) {
            for(File npcFile : npcFiles) {
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(npcFile));
                    String name = properties.getProperty("name");
                    String script = properties.getProperty("script");
                    String vehicleData = properties.getProperty("vehicle");
                    if(name != null && script != null) {
                        Npc npc = null;
                        if(vehicleData != null) {
                            String[] parts = vehicleData.split(",");
                            if(parts.length == 7) {
                                try {
                                    Vehicle vehicle = objectManager.createVehicle(
                                            Integer.parseInt(parts[0]),
                                            Float.parseFloat(parts[1]),
                                            Float.parseFloat(parts[2]),
                                            Float.parseFloat(parts[3]),
                                            Float.parseFloat(parts[4]),
                                            Integer.parseInt(parts[5]),
                                            Integer.parseInt(parts[6]),
                                            -1
                                    );
                                    npc = new Npc(name, script, vehicle);
                                } catch(NumberFormatException e) {
                                    getLogger().warn(name + " vehicle could not be parsed: " + e.getMessage());
                                }
                            }
                        } else {
                            npc = new Npc(name, script);
                        }
                        npcs.add(npc);
                    }
                } catch(IOException e) {
                    getLogger().error(e.getMessage());
                }
            }
        }
    }
}
