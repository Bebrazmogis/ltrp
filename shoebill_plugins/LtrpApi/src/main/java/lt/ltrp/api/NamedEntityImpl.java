package lt.ltrp.api;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class NamedEntityImpl extends EntityImpl {

    private String name;

    public NamedEntityImpl(int id, String name) {
        super(id);
        this.name = name;
    }

    public NamedEntityImpl(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
