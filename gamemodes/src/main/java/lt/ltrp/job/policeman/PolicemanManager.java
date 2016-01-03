package lt.ltrp.job.policeman;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.job.policeman.modelpreview.RoadblockModelPreview;
import lt.ltrp.plugin.streamer.DynamicLabel;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.util.event.EventManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class PolicemanManager  {

    public static final int JOB_ID = 2;
    private static PolicemanManager instance;

    public static PolicemanManager getInstance() {
        if(instance == null) {
            instance = new PolicemanManager();
        }
        return instance;
    }

    private EventManager eventManager;
    private OfficerJob job;
    private RoadblockModelPreview roadblockPreview;
    protected Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    protected Map<JobVehicle, DynamicSampObject> policeSirens = new HashMap<>();


    public PolicemanManager() {
        this.eventManager = LtrpGamemode.get().getEventManager().createChildNode();
        this.unitLabels = new HashMap<>();
        this.policeSirens = new HashMap<>();
        this.roadblockPreview = RoadblockModelPreview.get();

        this.job = LtrpGamemode.getDao().getJobDao().getPoliceFaction(JOB_ID);

        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            JobVehicle jobVehicle = JobVehicle.getById(e.getVehicle().getId());
            if(jobVehicle != null) {
                if(unitLabels.containsKey(jobVehicle)) {
                    unitLabels.get(jobVehicle).destroy();
                    unitLabels.remove(jobVehicle);
                }
                if(policeSirens.containsKey(jobVehicle)) {
                    policeSirens.get(jobVehicle).destroy();
                    policeSirens.remove(jobVehicle);
                }
            }
        });


    }

}
