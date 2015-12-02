package lt.ltrp;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class NamedEntity extends Entity {

    private String name;

    public NamedEntity(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
