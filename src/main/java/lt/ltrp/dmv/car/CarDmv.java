package lt.ltrp.dmv.car;


import lt.ltrp.dmv.*;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Bebras
 *         2015.12.26.
 */
public class CarDmv implements Dmv, CheckpointDmv, QuestionDmv {

    private static final ArrayList<LicenseType> LICENSE_TYPES = new ArrayList<>(2);

    static {
        LICENSE_TYPES.add(LicenseType.Car);
        LICENSE_TYPES.add(LicenseType.Motorcycle);
    }

    private DmvCheckpoint[] checkpoints;
    private List<DmvVehicle> vehicles;
    private List<DmvQuestion> questions;
    private Location location;
    private int id, drivingTestPrice, questionTestPrice;
    private String name;

    public CarDmv(int id) {
        this.id = id;
    }

    public CarDmv(int id, Location location, String name) {
        this.id = id;
        this.location = location;
        this.name = name;
    }

    public int getQuestionTestPrice() {
        return questionTestPrice;
    }

    public int getDrivingTestPrice() {
        return drivingTestPrice;
    }

    public void setDrivingTestPrice(int drivingTestPrice) {
        this.drivingTestPrice = drivingTestPrice;
    }

    public void setQuestionTestPrice(int questionTestPrice) {
        this.questionTestPrice = questionTestPrice;
    }

    @Override
    public AbstractCheckpointTest startCheckpointTest(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager) {
        return DrivingTest.create(player, vehicle, this, eventManager);
    }

    public int getCheckpointTestPrice() {
        return DrivingTest.PRICE;
    }

    @Override
    public QuestionTest startQuestionTest(LtrpPlayer player, EventManager eventManager) {
        return QuestionTest.create(player, this, eventManager);
    }

    public DmvCheckpoint[] getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(DmvCheckpoint[] checkpoints) {
        this.checkpoints = checkpoints;
    }

    public List<DmvQuestion> getQuestions() {
        return questions;
    }

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
    public List<DmvVehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void setVehicles(List<DmvVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public List<LicenseType> getLicenseType() {
        return LICENSE_TYPES;
    }
}
