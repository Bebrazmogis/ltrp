package lt.ltrp.property.data;

import lt.ltrp.property.object.House;
import lt.ltrp.property.event.WeedGrowEvent;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;

import java.time.Instant;
import java.util.Random;


/**
 * @author Bebras
 *         2015.12.05.
 *
 *
 */
public class HouseWeedSapling implements Destroyable{

    private static final int PLANT_POT_MODEL = 12;
    private static final int MIN_YIELD = 12;
    private static final int MAX_YIELD = 22;

    private int id;

    private House house;
    private Location location;
    private long plantTimestamp, growthTimestamp;
    private GrowthStage stage;
    private int plantedByUser, harvestedByUser;
    private int yield;
    private boolean destroyed;
    private EventManager eventManager;


    private DynamicObject weedObject, plantPotObject;
    private Timer growthTimer;

    private Timer.TimerCallback timerCallback = (interval) -> {
        this.stage = stage.next();
        if(stage == GrowthStage.Grown) {
            growthTimestamp = Instant.now().getEpochSecond();
            yield = new Random().nextInt(MAX_YIELD - MIN_YIELD) + MIN_YIELD;
        } else {
            setTimer();
        }
        eventManager.dispatchEvent(new WeedGrowEvent(house, this, isGrown()));
    };


    public HouseWeedSapling(Location location, House house, int plantedByUser, EventManager eventManager) {

        this.location = location;
        this.house = house;
        this.plantedByUser = plantedByUser;
        this.stage = GrowthStage.Seed;
        this.eventManager = eventManager;
        this.plantTimestamp = Instant.now().getEpochSecond();
    }

    public HouseWeedSapling() {

    }


    public void startGrowth() {
        if(growthTimer != null) {
            growthTimer.stop();
        }
        if(plantPotObject != null) {
            plantPotObject.destroy();
        }
        if(weedObject != null) {
            weedObject.destroy();
        }
        if(stage == GrowthStage.Grown)
            return;

        plantPotObject = DynamicObject.create(PLANT_POT_MODEL, location, new Vector3D());
        if(stage != GrowthStage.Seed) {
            weedObject = DynamicObject.create(stage.getModelId(), location, new Vector3D());
        }
        setTimer();
    }

    private void setTimer() {
        growthTimer = Timer.create(stage.getDuration(), timerCallback);
    }

    public boolean isGrown() {
        return stage == GrowthStage.Grown;
    }

    public int getYield() {
        return yield;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House h) {
        this.house = h;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPlantTimestamp(long plantTimestamp) {
        this.plantTimestamp = plantTimestamp;
    }

    public void setGrowthTimestamp(long growthTimestamp) {
        this.growthTimestamp = growthTimestamp;
    }

    public void setStage(int stage) {
        this.stage = GrowthStage.values()[stage];
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    public int getHarvestedByUser() {
        return harvestedByUser;
    }

    public void setHarvestedByUser(int harvestedByUser) {
        this.harvestedByUser = harvestedByUser;
    }

    public Location getLocation() {
        return location;
    }


    public long getPlantTimestamp() {
        return plantTimestamp;
    }


    public long getGrowthTimestamp() {
        return growthTimestamp;
    }

    public int getStage() {
        return stage.ordinal();
    }

    public int getPlantedByUser() {
        return plantedByUser;
    }

    public void setPlantedByUser(int plantedByUser) {
        this.plantedByUser = plantedByUser;
    }

    @Override
    public void destroy() {
        destroyed = true;
        if(growthTimer != null) {
            growthTimer.stop();
        }
        if(weedObject != null) {
            weedObject.destroy();
        }
        if(plantPotObject != null) {
            plantPotObject.destroy();
        }
        house.getWeedSaplings().remove(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    private enum GrowthStage {


        Seed(0, 30),
        Tiny(19839, 60),
        Small(19838, 60),
        Normal(19837, 60),
        Grown(19473, 0);

        private int modelId;
        private int duration;

        GrowthStage(int modelid, int growthduration) {
            this.modelId = modelid;
            this.duration = growthduration;
        }

        public int getModelId() {
            return modelId;
        }

        public int getDuration() {
            return duration;
        }

        public GrowthStage next() {
            return values()[ordinal()+1];
        }
    }


}
