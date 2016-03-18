package lt.ltrp.modelpreview.event;

import lt.ltrp.modelpreview.ModelPreview;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class ModelPreviewEvent extends PlayerEvent{

    private ModelPreview preview;

    protected ModelPreviewEvent(LtrpPlayer player, ModelPreview preview) {
        super(player);
        this.preview = preview;
    }

    public ModelPreview getPreview() {
        return preview;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }
}
