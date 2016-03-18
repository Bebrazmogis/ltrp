package lt.ltrp.shopplugin;

import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Label;

import java.util.Random;

/**
 * @author Bebras
 *         2016.03.15.
 */
public class VehicleShop implements Destroyable {

    private static final Random random = new Random();

    private int id;
    private String name;
    private Location location;
    private ShopVehicle[] soldVehicles;
    private AngledLocation[] spawnLocations;

    private Label label;

    public VehicleShop(Location location, String name) {
        this(0, name, location, new AngledLocation[0]);
    }

    public VehicleShop(int id, String name, Location location, AngledLocation[] spawnLocations) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.spawnLocations = spawnLocations;
        this.label = Label.create(name, Color.SIENNA, location, 32f, true);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(AngledLocation location) {
        this.location = location;
    }

    public ShopVehicle[] getVehicles() {
        return soldVehicles;
    }

    protected void setVehicles(ShopVehicle[] vehicles) {
        this.soldVehicles = vehicles;
    }

    protected void addVehicle(ShopVehicle vehicle) {
        ShopVehicle[] tmp = new ShopVehicle[soldVehicles.length + 1];
        tmp[0] = vehicle;
        for(int i = 0; i < soldVehicles.length; i++) {
            tmp[i-1] = soldVehicles[i];
        }
        setVehicles(tmp);
    }

    protected void removeVehicle(ShopVehicle vehicle) {
        ShopVehicle[] tmp = new ShopVehicle[soldVehicles.length - 1];
        for(int i = 0, j = 0; i < soldVehicles.length; i++) {
            if(!soldVehicles[i].equals(vehicle))
                tmp[j++] = soldVehicles[i];
        }
    }

    public AngledLocation[] getSpawnLocations() {
        return spawnLocations;
    }

    public AngledLocation getRandomSpawnLocation() {
        return spawnLocations[random.nextInt(spawnLocations.length)];
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof VehicleShop && ((VehicleShop) o).getId() == getId();
    }

    @Override
    public void destroy() {
        if(label != null)
            label.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return label.isDestroyed();
    }
}
