package lt.ltrp.job.data;

import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Timer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class TaxiFare {

    private static final int MAX_PASSENGERS = 10;

    private LtrpPlayer driver;
    private LtrpPlayer[] passengers;
    private Map<LtrpPlayer, Float> distanceTraveled;
    private Location lastLocation;
    private LtrpVehicle vehicle;
    private Timer timer;
    private int passengerCount;
    private int price;

    public TaxiFare(LtrpVehicle vehicle) {
        int seatCount = LtrpVehicleModel.getSeats(vehicle.getModelId());
        this.vehicle = vehicle;
        this.passengers = new LtrpPlayer[seatCount];
        this.distanceTraveled = new HashMap<>(seatCount);
    }

    public LtrpPlayer getDriver() {
        return driver;
    }

    public void setDriver(LtrpPlayer driver) {
        this.driver = driver;
        if(timer == null && passengerCount != 0) {
            startTimer();
        }
    }

    public LtrpPlayer[] getPassengers() {
        return passengers;
    }

    public boolean isPassenger(LtrpPlayer player) {
        for(int i = 0; i < passengerCount; i++) {
            if(passengers[ i ].equals(player)) {
                return true;
            }
        }
        return false;
    }

    public int getPrice(LtrpPlayer player) {
        if(distanceTraveled.containsKey(player)) {
            return Math.round(distanceTraveled.get(player) * getPrice());
        } else {
            return 0;
        }
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public boolean isFull() {
        return passengerCount == MAX_PASSENGERS;
    }

    public void addPassenger(LtrpPlayer passenger) {
        if(passengerCount < passengers.length) {
            passengers[ passengerCount ++ ] = passenger;
            if(timer == null) {
                startTimer();
            }
        }
    }

    public void removePassenger(LtrpPlayer player) {
        for(int i = 0; i < passengerCount; i++) {
            if(passengers[ i ].equals(player)) {
                for(int j = i + 1; j < passengerCount - 1; j++) {
                    passengers[ i ] = passengers[ j ];
                }
                passengers[--passengerCount] = null;
                break;
            }
        }
    }

    private void startTimer() {
        timer = Timer.create(1100, ticks -> {
            if(vehicle.isDestroyed()) {
                timer.stop();
            }
            Location newLocation = vehicle.getLocation();
            float distance = lastLocation.distance(newLocation);
            lastLocation = newLocation;
            for(LtrpPlayer passenger : passengers) {
                if(!distanceTraveled.containsKey(passenger)) {
                    distanceTraveled.put(passenger, distance);
                } else {
                    distanceTraveled.put(passenger, distanceTraveled.get(passenger) + distance);
                }
            }
        });
    }


}
