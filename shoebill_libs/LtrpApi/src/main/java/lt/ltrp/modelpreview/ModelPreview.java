package lt.ltrp.modelpreview;

import net.gtaun.shoebill.entities.Destroyable;

/**
 * @author Bebras
 *         2015.12.30.
 */
public interface ModelPreview extends Destroyable {

    boolean isShown();
    void hide();
    void show(int page);
    default void show() {
        show(0);
    }


}
