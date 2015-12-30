package lt.ltrp;

import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.30.
 */
public interface ModelPreview {

    void show(LtrpPlayer player);
    void hide(LtrpPlayer player);
    boolean isShown(LtrpPlayer player);

}
