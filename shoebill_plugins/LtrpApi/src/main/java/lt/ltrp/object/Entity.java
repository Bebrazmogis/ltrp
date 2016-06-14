package lt.ltrp.object;

/**
 * @author Bebras
 *         2016.03.23.
 */
public interface Entity {

    static final int INVALID_ID = 0;

    void setUUID(int uuid);
    int getUUID();

}
