package lt.ltrp.data;

import net.gtaun.shoebill.data.Location;

/**
 * @author Bebras
 *         2015.12.18.
 */
public class NamedLocation extends Location {

    private String name;

    public NamedLocation() {

    }

    public NamedLocation(String name, Location location) {
        super(location);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
