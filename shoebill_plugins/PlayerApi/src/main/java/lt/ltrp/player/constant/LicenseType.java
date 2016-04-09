package lt.ltrp.player.constant;

/**
 * @author Bebras
 *         2015.12.13.
 */
public enum LicenseType {

    Car("Automobilio"),
    Aircraft("Oro transporto"),
    Ship("Jûrininkystës"),
    Motorcycle("Motociklo"),
    Gun("Ginklo"),
    ;

    private String name;

    LicenseType(String s) {
        this.name = s;
    }

    public String getName() {
        return name;
    }
}
