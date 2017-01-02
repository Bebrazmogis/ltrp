package lt.ltrp.object.impl;

import lt.ltrp.data.TrashMission;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.object.Checkpoint;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class PlayerTrashMission {

    private TrashMission mission;
    private int progress;
    private LtrpPlayer player;
    private LtrpVehicle vehicle;
    private Checkpoint checkpoint;

    public PlayerTrashMission(TrashMission mission, LtrpPlayer player, LtrpVehicle vehicle) {
        this.mission = mission;
        this.progress = 0;
        this.player = player;
        this.vehicle = vehicle;
    }

    public LtrpVehicle getVehicle() {
        return vehicle;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public boolean pickedAll() {
        return getProgress() == mission.getGarbageCount();
    }

    public TrashMission getMission() {
        return mission;
    }

    public int getProgress() {
        return progress;
    }

    public void incrementProgress() {
        this.progress++;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }
}

