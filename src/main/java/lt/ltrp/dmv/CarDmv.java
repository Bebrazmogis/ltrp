package lt.ltrp.dmv;

import lt.ltrp.constant.LicenseType;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Bebras
 *         2015.12.26.
 */
public class CarDmv extends QuestionCheckpointDmvImpl {

    private static final ArrayList<LicenseType> LICENSE_TYPES = new ArrayList<>(2);

    static {
        LICENSE_TYPES.add(LicenseType.Car);
        LICENSE_TYPES.add(LicenseType.Motorcycle);
    }

    public CarDmv(int id) {
        super(id, null, null);
    }

    public CarDmv(int id, Location location, String name) {
        super(id, location, name);
    }

    @Override
    public AbstractCheckpointTest startCheckpointTest(LtrpPlayer player, LtrpVehicle vehicle, EventManager eventManager) {
        return DrivingTest.create(player, vehicle, this, eventManager);
    }

    @Override
    public int getCheckpointTestPrice() {
        return DrivingTest.PRICE;
    }

    @Override
    public QuestionTest startQuestionTest(LtrpPlayer player, EventManager eventManager) {
        return QuestionTest.create(player, this, eventManager);
    }

    @Override
    public int getQuestionTestPrice() {
        return QuestionTest.PRICE;
    }

    @Override
    public List<LicenseType> getLicenseType() {
        return LICENSE_TYPES;
    }
}
