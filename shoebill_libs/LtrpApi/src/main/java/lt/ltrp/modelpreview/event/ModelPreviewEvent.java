package lt.ltrp.modelpreview.event;

import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.modelpreview.ModelPreview;
import lt.ltrp.object.LtrpPlayer;


/**
 * @author Bebras
 *         2015.12.30.
 */
public class ModelPreviewEvent extends PlayerEvent {

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
