package lt.ltrp.modelpreview.event;

import lt.ltrp.modelpreview.ModelPreview;
import net.gtaun.shoebill.entities.Player;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class ModelPreviewCancelEvent extends ModelPreviewEvent {


    public ModelPreviewCancelEvent(Player player, ModelPreview preview) {
        super(player, preview);
    }
}
