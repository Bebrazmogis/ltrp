package lt.ltrp.textdraw;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.PlayerTextdraw;

/**
 * @author Bebras
 *         2016.04.29.
 */
public abstract class AbstractGameTextCustomStyle implements Destroyable {

    private LtrpPlayer player;
    private PlayerTextdraw textdraw;
    private boolean destroyed;

    public AbstractGameTextCustomStyle(LtrpPlayer player, PlayerTextdraw textdraw) {
        this.player = player;
        this.textdraw = textdraw;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    protected PlayerTextdraw getTextdraw() {
        return textdraw;
    }

    public void show() {
        show(" ");
    }

    public void show(String text) {
        getTextdraw().setText(text);
        getTextdraw().show();
    }

    public void hide() {
        getTextdraw().hide();
    }

    public abstract int getStyleId();

    @Override
    public void destroy() {
        if(isDestroyed()) return;
        destroyed = true;
        this.textdraw.destroy();
        this.textdraw = null;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
