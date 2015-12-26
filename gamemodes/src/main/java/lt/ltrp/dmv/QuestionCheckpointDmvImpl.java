package lt.ltrp.dmv;

import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.Location;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.25.
 */
public abstract class QuestionCheckpointDmvImpl implements QuestionCheckpointDmv {

    private List<DmvCheckpoint> checkpoints;
    private List<LtrpVehicle> vehicles;
    private List<DmvQuestion> questions;
    private Location location;
    private int id;
    private String name;

    public QuestionCheckpointDmvImpl(int id, Location location, String name) {
        this.id = id;
        this.location = location;
        this.name = name;
    }

    @Override
    public List<DmvCheckpoint> getCheckpoints() {
        return checkpoints;
    }

    @Override
    public void setCheckpoints(List<DmvCheckpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }

    @Override
    public List<DmvQuestion> getQuestions() {
        return questions;
    }

    @Override
    public void setQuestions(List<DmvQuestion> questions) {
        this.questions = questions;
    }

    @Override
    public void setLocation(Location loc) {
        this.location = loc;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<LtrpVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(List<LtrpVehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
