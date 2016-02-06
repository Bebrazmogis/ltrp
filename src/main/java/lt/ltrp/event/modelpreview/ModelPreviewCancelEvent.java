package lt.ltrp.event.modelpreview;

import lt.ltrp.ModelPreview;
import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class ModelPreviewCancelEvent extends ModelPreviewEvent {


    public ModelPreviewCancelEvent(LtrpPlayer player, ModelPreview preview) {
        super(player, preview);
    }
}
