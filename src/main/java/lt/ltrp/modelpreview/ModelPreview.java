package lt.ltrp.modelpreview;

import net.gtaun.shoebill.object.Destroyable;

/**
 * @author Bebras
 *         2015.12.30.
 */
public interface ModelPreview extends Destroyable {

    void show();
    void show(int page);
    void hide();
    boolean isShown();

}
