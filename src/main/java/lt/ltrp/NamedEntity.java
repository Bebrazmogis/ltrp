package lt.ltrp;

/**
 * @author Bebras
 *         2016.03.23.
 */
public interface NamedEntity extends Entity {

    /**
     *
     * @return returns the entities name
     */
    String getName();

    /**
     * Sets the entities name
     * @param name the name to set
     */
    void setName(String name);

}
