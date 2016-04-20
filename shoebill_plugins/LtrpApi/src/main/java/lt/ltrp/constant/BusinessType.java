package lt.ltrp.constant;

/**
 * @author Bebras
 *         2016.04.19.
 */
public enum  BusinessType {

    None(1, "nëra"),
    Supermarket(2, "Parduotuvë"),
    Cafe(3, "Kavinë"),
    Bar(4, "Baras"),
    ClothesShop(5, "Drabuþiø parduotuvë"),
    BarberShop(6, "Kirpykla");

    String name;
    int id;

    BusinessType(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static BusinessType get(int id) {
        for(BusinessType t : values()) {
            if(t.getId() == id)
                return t;
        }
        return null;
    }
}
