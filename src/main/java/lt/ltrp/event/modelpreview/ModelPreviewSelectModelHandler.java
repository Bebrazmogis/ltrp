package lt.ltrp.event.modelpreview;

import lt.ltrp.ModelPreview;

/**
 * @author Bebras
 *         2015.12.30.
 */
@FunctionalInterface
public interface ModelPreviewSelectModelHandler {

    void handle(ModelPreview preview, int model);

}
