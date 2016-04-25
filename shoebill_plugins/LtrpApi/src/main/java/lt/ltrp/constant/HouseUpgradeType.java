package lt.ltrp.constant;

/**
 * @author Bebras
 *         2015.12.03.
 */
public enum  HouseUpgradeType {


    Refrigerator(1, "Ðaldytuvas"),
    Radio(2, "Radijas");

    private int id;
    private String name;

    HouseUpgradeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static HouseUpgradeType get(int id) {
        for(HouseUpgradeType h : values()) {
            if(h.id == id) {
                return h;
            }
        }
        return null;
    }

}
