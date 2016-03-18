package lt.ltrp.modelpreview;

import lt.ltrp.data.Color;
import lt.ltrp.modelpreview.event.ModelPreviewCancelEvent;
import lt.ltrp.modelpreview.event.ModelPreviewSelectModelEvent;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.TextDrawAlign;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.event.player.PlayerClickPlayerTextDrawEvent;
import net.gtaun.shoebill.event.player.PlayerClickTextDrawEvent;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.shoebill.object.Textdraw;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.*;

/**
 * @author Bebras
 *         2016.03.05.
 */
public class BasicModelPreview implements ModelPreview {

    private static final Textdraw background;
    private static final Textdraw nextButton, prevButton;
    private static final Textdraw exitButton;

    private static final Color DEFAULT_BUTTON_COLOR = new Color(0x4A5A6BFF);
    private static final float DEFAULT_MODEL_WIDTH = 60;
    private static final float DEFAULT_MODEL_HEIGHT = 70;
    private static final float BACKGROUND_WIDTH = 550f;
    private static final float BACKGROUND_HEIGHT = 180f;
    private static final float BASE_X = 75f;
    private static final float BASE_Y = 150f;
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(0x4A5A6BBB);
    private static final Color DEFAULT_MODEL_BACKGROUND_COLOR = new Color(0x88888899);
    private static final Color DEFAULT_SELECTION_COLOR = new Color(0xFFFF00AA);


    static {
        background = Textdraw.create(BASE_X, BASE_Y, "              ~n~");
        background.setUseBox(true);
        background.setLetterSize(5f, 5f);
        background.setFont(TextDrawFont.DIPLOMA);
        background.setShadowSize(0);
        background.setOutlineSize(0);
        background.setTextSize(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        nextButton = Textdraw.create(520f, 410f, "Toliau");
        nextButton.setUseBox(true);
        nextButton.setBoxColor(new Color(0x000000FF));
        nextButton.setBackgroundColor(new Color(0x000000FF));
        nextButton.setLetterSize(0.4f, 1.1f);
        nextButton.setFont(TextDrawFont.DIPLOMA);
        nextButton.setShadowSize(0);
        nextButton.setOutlineSize(0);
        nextButton.setSelectable(true);
        nextButton.setAlignment(TextDrawAlign.CENTER);
        nextButton.setTextSize(50f, 16f); //  // The width and height are reversed for centering.. something the game does <g>

        prevButton = Textdraw.create(460f, 410f, "Atgal");
        prevButton.setUseBox(true);
        prevButton.setBoxColor(new Color(0x000000FF));
        prevButton.setBackgroundColor(new Color(0x000000FF));
        prevButton.setLetterSize(0.4f, 1.1f);
        prevButton.setFont(TextDrawFont.DIPLOMA);
        prevButton.setShadowSize(0);
        prevButton.setOutlineSize(0);
        prevButton.setSelectable(true);
        prevButton.setAlignment(TextDrawAlign.CENTER);
        prevButton.setTextSize(50f, 16f); //  // The width and height are reversed for centering.. something the game does <g>

        exitButton = Textdraw.create(400f, 410f, "Baigti");
        prevButton.setUseBox(true);
        prevButton.setBoxColor(new Color(0x000000FF));
        prevButton.setBackgroundColor(new Color(0x000000FF));
        prevButton.setLetterSize(0.4f, 1.1f);
        prevButton.setFont(TextDrawFont.DIPLOMA);
        prevButton.setShadowSize(0);
        prevButton.setOutlineSize(0);
        prevButton.setSelectable(true);
        prevButton.setAlignment(TextDrawAlign.CENTER);
        prevButton.setTextSize(50f, 16f); //  // The width and height are reversed for centering.. something the game does <g>
    }

    public static BasicModelPreviewBuilder create(LtrpPlayer player, EventManager eventManager, float modelWidth, float modelHeight) {
        return new BasicModelPreviewBuilder(player, eventManager, modelWidth, modelHeight);
    }

    public static BasicModelPreviewBuilder create(LtrpPlayer player, EventManager event) {
        return new BasicModelPreviewBuilder(player, event);
    }


    public static class BasicModelPreviewBuilder {
        private BasicModelPreview preview;

        private BasicModelPreviewBuilder(LtrpPlayer player, EventManager eventManager, float modelWidth, float modelHeight) {
            preview = new BasicModelPreview(player, eventManager, modelWidth, modelHeight);
        }

        private BasicModelPreviewBuilder(LtrpPlayer player, EventManager eventManager) {
            preview = new BasicModelPreview(player, eventManager);
        }

        public BasicModelPreviewBuilder model(int modelId) {
            preview.addModel(modelId);
            return this;
        }

        public BasicModelPreviewBuilder model(int model, SelectModelHandler handler) {
            preview.addModel(model, handler);
            return this;
        }

        public BasicModelPreviewBuilder onClickCancel(ClickCancelHandler handler) {
            preview.setCancelHandler(handler);
            return this;
        }

        public BasicModelPreviewBuilder models(Collection<Integer> models) {
            preview.addModels(models);
            return this;
        }

        public BasicModelPreviewBuilder models(Map<Integer, SelectModelHandler> models) {
            preview.addModels(models);
            return this;
        }

        public BasicModelPreviewBuilder models(int[] models) {
            preview.addModels(models);
            return this;
        }

        public BasicModelPreviewBuilder onSelectModel(SelectModelHandler handler) {
            preview.setSelectHandler(handler);
            return this;
        }

        public BasicModelPreviewBuilder buttonColor(Color color) {
            preview.setButtonColor(color);
            return this;
        }

        public BasicModelPreviewBuilder backgroundColor(Color color) {
            preview.setBackgroundColor(color);
            return this;
        }

        public BasicModelPreviewBuilder modelBackgroundColor(Color color) {
            preview.setModelBackgroundColor(color);
            return this;
        }

        public BasicModelPreviewBuilder selectionColor(Color c) {
            preview.setSelectionColor(c);
            return this;
        }

        public BasicModelPreview build() {
            return preview;
        }
    }


    private List<Integer> modelIds;
    private Map<Integer, SelectModelHandler> handlers;
    private Map<Integer, PlayerTextdraw> modelTextdraws;
    private LtrpPlayer player;
    private int page, itemsPerPage;
    private Color buttonColor, backgroundColor, modelBackgroundColor, selectionColor;
    private EventManagerNode eventManager;
    private float modelWidth, modelHeight;
    private ClickCancelHandler cancelHandler;


    private SelectModelHandler selectHandler;
    private boolean shown, destroyed;


    protected BasicModelPreview(LtrpPlayer player, EventManager eventManager, float modelWidth, float modelHeight) {
        this.eventManager = eventManager.createChildNode();
        this.player = player;
        this.modelHeight = modelHeight;
        this.modelWidth = modelWidth;
        this.modelIds = new ArrayList<>();
        this.handlers = new HashMap<>();
        this.modelTextdraws = new HashMap<>();
        this.buttonColor = DEFAULT_BUTTON_COLOR;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.modelBackgroundColor = DEFAULT_MODEL_BACKGROUND_COLOR;
        this.selectionColor = DEFAULT_SELECTION_COLOR;
        int itemsPerLine = (int)((BACKGROUND_WIDTH -(BASE_X + 25f)) / (modelWidth + 1));
        int itemsPerColumn = (int)((BACKGROUND_WIDTH - BASE_Y - modelHeight * 0.33f) / (modelHeight + 1));
        this.itemsPerPage = itemsPerColumn * itemsPerLine;
    }

    protected BasicModelPreview(LtrpPlayer player, EventManager eventManager1) {
        this(player, eventManager1, DEFAULT_MODEL_WIDTH, DEFAULT_MODEL_HEIGHT);
    }

    @Override
    public void show() {
        show(0);
    }

    @Override
    public void show(int page) {
        if(!isShown()) {
            background.setBackgroundColor(backgroundColor);
            exitButton.setColor(buttonColor);
            nextButton.setColor(buttonColor);
            prevButton.setColor(buttonColor);

            background.show(player);
            exitButton.show(player);
            nextButton.show(player);
            prevButton.show(player);

            player.selectTextDraw(selectionColor);

            for(int i = 0; i < itemsPerPage; i++) {
                PlayerTextdraw td =  modelTextdraws.get(modelIds.get(i));
                if(td != null)
                    td.show();
            }
            shown = true;
            this.page = 0;
            addEventHandlers();
        } else {
            // We hide the previous page
            for(int i = this.page * itemsPerPage; i <  (this.page+1) * itemsPerPage; i++) {
                PlayerTextdraw td =  modelTextdraws.get(modelIds.get(i));
                if(td != null)
                    td.show();
            }

            // We show the new page
            for(int i = page * itemsPerPage; i < (page + 1) * itemsPerPage; i++) {
                PlayerTextdraw td =  modelTextdraws.get(modelIds.get(i));
                if(td != null)
                    td.show();
            }

            this.page = page;
        }
    }

    @Override
    public void hide() {
        background.hide(player);
        exitButton.hide(player);
        nextButton.hide(player);
        prevButton.hide(player);
        for(int i = this.page * itemsPerPage; i <  (this.page+1) * itemsPerPage; i++) {
            PlayerTextdraw td =  modelTextdraws.get(modelIds.get(i));
            if(td != null)
                td.hide();
        }
        eventManager.cancelAll();
    }

    @Override
    public boolean isShown() {
        return shown;
    }

    @Override
    public void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    @Override
    public void destroy() {
        if(isShown()) {
            hide();
        }
        modelTextdraws.values().forEach(PlayerTextdraw::destroy);
        destroyed = true;
        eventManager.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    private void addEventHandlers() {
        this.eventManager.registerHandler(PlayerClickTextDrawEvent.class, e -> {
            Textdraw td = e.getTextdraw();
            if(isShown() && td != null) {
                if(td.equals(exitButton)) {
                    if(cancelHandler != null)
                        cancelHandler.onClickCancel(this);
                    eventManager.dispatchEvent(new ModelPreviewCancelEvent(player, this));
                    hide();
                } else if(td.equals(nextButton)) {
                    show(page + 1);
                } else if(td.equals(prevButton)) {
                    show(page - 1);
                }
            }
        });

        this.eventManager.registerHandler(PlayerClickPlayerTextDrawEvent.class, e -> {
            PlayerTextdraw clickedTd = e.getPlayerTextdraw();
            if(isShown() && clickedTd != null) {
                for(int i = this.page * itemsPerPage; i <  (this.page+1) * itemsPerPage; i++) {
                    int modelId = modelIds.get(i);
                    PlayerTextdraw td =  modelTextdraws.get(modelId);
                    if(td != null && td.equals(clickedTd)) {
                        SelectModelHandler handler = handlers.get(modelId);
                        if(handler != null)
                            handler.onSelectModel(this, modelId);
                        else if(selectHandler != null)
                            selectHandler.onSelectModel(this, modelId);
                        eventManager.dispatchEvent(new ModelPreviewSelectModelEvent(player, this, modelId));
                    }
                }
            }
        });
    }

    public void addModel(int model) {
        addModel(model, null);
    }

    public void addModel(int model, SelectModelHandler handler) {
        modelIds.add(model);
        if(handler != null)
            this.handlers.put(model, handler);

        createModelTextDraw(model);
    }

    public void addModels(Collection<Integer> models) {
        models.forEach(this::addModel);
    }

    public void addModels(Map<Integer, SelectModelHandler> models) {
        models.forEach(this::addModel);
    }

    public void addModels(int[] models) {
        for(int model : models)
            addModel(model);
    }

    private void createModelTextDraw(int modelId) {
        int itemsPerLine = (int)((BACKGROUND_WIDTH -(BASE_X + 25f)) / (modelWidth + 1));
        int itemsPerColumn = (int)((BACKGROUND_WIDTH - BASE_Y - modelHeight * 0.33f) / (modelHeight + 1));
        int currentPageItemIndex = modelIds.size() / itemsPerPage;
        int lineIndex = currentPageItemIndex / itemsPerColumn;
        int columnIndex = currentPageItemIndex - lineIndex * itemsPerColumn;
        float x = lineIndex * (modelWidth + 1);
        float y = columnIndex * (modelHeight + 1);
        PlayerTextdraw textdraw = PlayerTextdraw.create(player, x, y,  " ");
        textdraw.setTextSize(modelWidth, modelHeight);
        textdraw.setFont(TextDrawFont.MODEL_PREVIEW);
        textdraw.setBackgroundColor(modelBackgroundColor);
        textdraw.setPreviewModel(modelId);
        modelTextdraws.put(modelId, textdraw);
    }

    public void setSelectHandler(SelectModelHandler selectHandler) {
        this.selectHandler = selectHandler;
    }

    public void setCancelHandler(ClickCancelHandler handler) {
        this.cancelHandler = handler;
    }

    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
    }

    public void setBackgroundColor(Color backgroundColor) {

        this.backgroundColor = backgroundColor;
    }

    public void setModelBackgroundColor(Color modelBackgroundColor) {
        this.modelBackgroundColor = modelBackgroundColor;
    }

    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
    }

    @FunctionalInterface
    public interface SelectModelHandler {
        void onSelectModel(ModelPreview modelPreview, int modelId);
    }

    @FunctionalInterface
    public interface ClickCancelHandler {
        void onClickCancel(ModelPreview preview);
    }

}
