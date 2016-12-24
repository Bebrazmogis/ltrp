package lt.ltrp.colorpicker;


import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.constant.VehicleColor;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.entities.Textdraw;
import net.gtaun.shoebill.event.player.PlayerClickTextDrawEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.List;

/**
 * @author Bebras
 *         2016.02.08.
 *
 */
public class ColorPicker {

    private static final ColorPickerTextDraws TEXT_DRAWS = new ColorPickerTextDraws();

   // private static final Textdraw[] TEXTDRAWS = new Textdraw[VehicleColor.getColors().length];


    private EventManagerNode eventManagerNode;
    private LtrpPlayer player;
    private ColorPickHandler handler;
    private ColorPickerCancelHandler cancelHandler;
    private int currentPage;
    private List<Color> colors;

    @Deprecated
    public static AbstractColorPickerBuilder create(LtrpPlayer player, EventManager manager, ColorPickHandler callback) {
        return new ColorPickerBuilder(new ColorPicker(player, manager.createChildNode(), callback));
    }

    public static AbstractColorPickerBuilder create(LtrpPlayer player, EventManager eventManager, List<Color> colors) {
        return new ColorPickerBuilder(new ColorPicker(player ,eventManager, colors));
    }

    @Deprecated
    protected ColorPicker(LtrpPlayer player, EventManagerNode node, ColorPickHandler callback) {
        this.player = player;
        this.eventManagerNode = node;
        this.handler = callback;
    }

    protected ColorPicker(LtrpPlayer player, EventManager eventManager, List<Color> colors) {
        this.player = player;
        this.eventManagerNode = eventManager.createChildNode();
        this.colors = colors;
    }


    public void setSelectHandler(ColorPickHandler handler) {
        this.handler = handler;
    }

    public void setCancelHandler(ColorPickerCancelHandler cancelHandler) {
        this.cancelHandler = cancelHandler;
    }

    public void addColor(Color color) {
        colors.add(color);
    }

    public void show() {
        int index = 0;
        for (Color color : colors) {
            if(index > ColorPickerTextDraws.MAX_COLORS)
                break;
            TEXT_DRAWS.colorTextDraws[index++].setBackgroundColor(color);
        }

        show(0);
        player.selectTextDraw(Color.YELLOW);
        eventManagerNode.registerHandler(PlayerClickTextDrawEvent.class, e -> {
            if(e.getTextdraw() == null) {
                hide();
                return;
            }
            for(int i = currentPage * ColorPickerTextDraws.ITEMS_PER_PAGE; i < (currentPage+1) * ColorPickerTextDraws.ITEMS_PER_PAGE && i < TEXT_DRAWS.colorTextDraws.length; i++) {
                if(e.getTextdraw().equals(TEXT_DRAWS.colorTextDraws[i])) {
                    hide();
                    if(handler != null) {
                        handler.colorSelected(this, i);
                    }
                }
            }
            if(e.getTextdraw().equals(TEXT_DRAWS.nextButton) && currentPage < TEXT_DRAWS.MAX_PAGE) {
                show(currentPage + 1);
            } else if(e.getTextdraw().equals(TEXT_DRAWS.prevButton) && currentPage > 0) {
                show(currentPage - 1);
            } else if(e.getTextdraw().equals(TEXT_DRAWS.exitButton)) {
                if(cancelHandler != null) cancelHandler.onColorPickCancel(this);
                hide();
            }
        });
    }

    protected void show(int page) {
        currentPage = page;
        for(int i = page * ColorPickerTextDraws.ITEMS_PER_PAGE; i < (page + 1) * ColorPickerTextDraws.ITEMS_PER_PAGE; i++) {
            int prevPageTdIndex = i - ColorPickerTextDraws.ITEMS_PER_PAGE,
                    nextPageTdIndex = i + ColorPickerTextDraws.ITEMS_PER_PAGE;
            if(prevPageTdIndex >= 0 && TEXT_DRAWS.colorTextDraws[prevPageTdIndex].isShownForPlayer(player)) {
                TEXT_DRAWS.colorTextDraws[prevPageTdIndex].hide(player);
            }
            if(nextPageTdIndex < TEXT_DRAWS.colorTextDraws.length && TEXT_DRAWS.colorTextDraws[nextPageTdIndex].isShownForPlayer(player)) {
                TEXT_DRAWS.colorTextDraws[nextPageTdIndex].hide(player);
            }
            if(i < TEXT_DRAWS.colorTextDraws.length)
                TEXT_DRAWS.colorTextDraws[i].show(player);
        }
        if(!TEXT_DRAWS.prevButton.isShownForPlayer(player)) {
            TEXT_DRAWS.prevButton.show(player);
        }
        if(!TEXT_DRAWS.nextButton.isShownForPlayer(player)) {
            TEXT_DRAWS.nextButton.show(player);
        }
        if(!TEXT_DRAWS.exitButton.isShownForPlayer(player))
            TEXT_DRAWS.exitButton.show(player);
    }

    public void hide() {
        player.cancelSelectTextDraw();
        for(Textdraw td : TEXT_DRAWS.colorTextDraws) {
            if(td.isShownForPlayer(player)) {
                td.hide(player);
            }
        }
        TEXT_DRAWS.nextButton.hide(player);
        TEXT_DRAWS.prevButton.hide(player);
        TEXT_DRAWS.exitButton.hide(player);
        eventManagerNode.cancelAll();
    }

    @SuppressWarnings("unchecked")
    public static class AbstractColorPickerBuilder<ColorPickerType extends ColorPicker, ColorPickerBuilderType extends AbstractColorPickerBuilder> {

        private ColorPickerType colorPicker;

        public AbstractColorPickerBuilder(ColorPickerType colorPicker) {
            this.colorPicker = colorPicker;
        }

        public ColorPickerBuilderType color(Color c) {
            colorPicker.addColor(c);
            return (ColorPickerBuilderType)this;
        }

        public ColorPickerBuilderType onSelectColor(ColorPickHandler handler) {
            colorPicker.setSelectHandler(handler);
            return (ColorPickerBuilderType)this;
        }

        public ColorPickerBuilderType onCancel(ColorPickerCancelHandler handler) {
            colorPicker.setCancelHandler(handler);
            return (ColorPickerBuilderType)this;
        }

        public ColorPickerType build() {
            return colorPicker;
        }

    }

    private static class ColorPickerBuilder extends AbstractColorPickerBuilder<ColorPicker, ColorPickerBuilder> {
        public ColorPickerBuilder(ColorPicker colorPicker) {
            super(colorPicker);
        }
    }

    @FunctionalInterface
    public interface ColorPickHandler {
        void colorSelected(ColorPicker colorpicker, int color);
    }

    @FunctionalInterface
    public interface ColorPickerCancelHandler {
        void onColorPickCancel(ColorPicker picker);
    }

    static class ColorPickerTextDraws {
        static final int MAX_COLORS = 255;
        static final float X = 220f;
        static final float Y = 100f;
        static final float MARGIN = 1f;
        static final int ITEMS_PER_PAGE = 25;


        final int MAX_PAGE = MAX_COLORS / ITEMS_PER_PAGE;
        Textdraw[] colorTextDraws;
        Textdraw nextButton, prevButton, exitButton;

        public ColorPickerTextDraws() {
            colorTextDraws = new Textdraw[MAX_COLORS];

            float x = X + MARGIN;
            float y = Y + MARGIN;
            int count = 0;
            for(Color c : VehicleColor.INSTANCE.get()) {
                System.out.println("COUNT: " + count + " id:" + VehicleColor.INSTANCE.getId(c));
                colorTextDraws[count] = Textdraw.create(x, y, " ");
                colorTextDraws[count].setFont(TextDrawFont.MODEL_PREVIEW);
                //colorTextDraws[count].setPreviewModel(492);
                colorTextDraws[count].setPreviewModelRotation(0f, 0f, 45f, 0.8f);
                //colorTextDraws[count].setPreviewVehicleColor(VehicleColor.getIdFromColor(c), VehicleColor.getIdFromColor(c));
                colorTextDraws[count].setBackgroundColor(new Color(0, 0, 0, 0x77));
                colorTextDraws[count].setTextSize(40f, 40f);
                colorTextDraws[count].setSelectable(true);
                x += 40f + MARGIN;
                count++;
                if(count > 0 && count % 5 == 0) {
                    x = X + MARGIN;
                    y += 40f + MARGIN;
                }
                if(count % ITEMS_PER_PAGE == 0) {
                    x = X + MARGIN;
                    y = Y + MARGIN;
                }
            }
            x = 5 * MARGIN + 40 * 5 + 30f;
            y = 5 * MARGIN + 40 * 5 + 110f;
            prevButton = Textdraw.create(x, y, "Atgal");
            prevButton.setFont(TextDrawFont.BANK_GOTHIC);
            prevButton.setShadowSize(0);
            prevButton.setTextSize(x+80f, 15f);
            prevButton.setSelectable(true);

            x += 120f;
            nextButton = Textdraw.create(x, y, "Toliau");
            nextButton.setFont(TextDrawFont.BANK_GOTHIC);
            nextButton.setShadowSize(0);
            nextButton.setTextSize(x+80f, 8f);
            nextButton.setSelectable(true);

            x = X + (MARGIN + 40) * 5 + 3f;
            exitButton = Textdraw.create(x ,Y-5f, "LD_BEAT:cross");
            exitButton.setFont(TextDrawFont.SPRITE_DRAW);
            exitButton.setTextSize(15f, 15f);
            exitButton.setSelectable(true);
        }

        @Override
        protected void finalize() throws Throwable {
            for(Textdraw td : colorTextDraws) {
                td.destroy();
            }
            nextButton.destroy();
            prevButton.destroy();
            exitButton.destroy();
            super.finalize();
        }
    }


}
