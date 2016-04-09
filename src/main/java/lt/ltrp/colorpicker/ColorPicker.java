package lt.ltrp.colorpicker;


import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.constant.VehicleColor;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerClickTextDrawEvent;
import net.gtaun.shoebill.object.Textdraw;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class ColorPicker {

    private static final Textdraw[] TEXTDRAWS = new Textdraw[VehicleColor.getColors().length];
    private static final Textdraw NEXT_PAGE, PREV_PAGE, EXIT_BUTTON;
    private static final float X = 220f;
    private static final float Y = 100f;
    private static final float MARGIN = 1f;
    private static final int ITEMS_PER_PAGE = 25;
    private static final int MAX_PAGE = TEXTDRAWS.length / ITEMS_PER_PAGE;

    static {
        float x = X + MARGIN;
        float y = Y + MARGIN;
        int count = 0;
        for(Color c : VehicleColor.getColors()) {
            System.out.println("COUNT: " + count + " id:" + VehicleColor.getIdFromColor(c));
            TEXTDRAWS[count] = Textdraw.create(x, y, " ");
            TEXTDRAWS[count].setFont(TextDrawFont.MODEL_PREVIEW);
            TEXTDRAWS[count].setPreviewModel(492);
            TEXTDRAWS[count].setPreviewModelRotation(0f, 0f, 45f, 0.8f);
            TEXTDRAWS[count].setPreviewVehicleColor(VehicleColor.getIdFromColor(c), VehicleColor.getIdFromColor(c));
            TEXTDRAWS[count].setBackgroundColor(new Color(0, 0, 0, 0x77));
            TEXTDRAWS[count].setTextSize(40f, 40f);
            TEXTDRAWS[count].setSelectable(true);
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
        PREV_PAGE = Textdraw.create(x, y, "Atgal");
        PREV_PAGE.setFont(TextDrawFont.BANK_GOTHIC);
        PREV_PAGE.setShadowSize(0);
        PREV_PAGE.setTextSize(x+80f, 15f);
        PREV_PAGE.setSelectable(true);

        x += 120f;
        NEXT_PAGE = Textdraw.create(x, y, "Toliau");
        NEXT_PAGE.setFont(TextDrawFont.BANK_GOTHIC);
        NEXT_PAGE.setShadowSize(0);
        NEXT_PAGE.setTextSize(x+80f, 8f);
        NEXT_PAGE.setSelectable(true);

        x = X + (MARGIN + 40) * 5 + 3f;
        EXIT_BUTTON = Textdraw.create(x ,Y-5f, "LD_BEAT:cross");
        EXIT_BUTTON.setFont(TextDrawFont.SPRITE_DRAW);
        EXIT_BUTTON.setTextSize(15f, 15f);
        EXIT_BUTTON.setSelectable(true);
    }

    private EventManagerNode eventManagerNode;
    private LtrpPlayer player;
    private ColorPickHandler handler;
    private int currentPage;

    public static ColorPicker create(LtrpPlayer player, EventManager manager, ColorPickHandler callback) {
        return new ColorPicker(player, manager.createChildNode(), callback);
    }

    private ColorPicker(LtrpPlayer player, EventManagerNode node, ColorPickHandler callback) {
        this.player = player;
        this.eventManagerNode = node;
        this.handler = callback;
    }

    public void show() {
        show(0);
        player.selectTextDraw(Color.YELLOW);
        eventManagerNode.registerHandler(PlayerClickTextDrawEvent.class, e -> {
            if(e.getTextdraw() == null) {
                hide();
                return;
            }
            for(int i = currentPage * ITEMS_PER_PAGE; i < (currentPage+1) * ITEMS_PER_PAGE && i < TEXTDRAWS.length; i++) {
                if(e.getTextdraw().equals(TEXTDRAWS[i])) {
                    hide();
                    if(handler != null) {
                        handler.colorSelected(i);
                    }
                }
            }
            if(e.getTextdraw().equals(NEXT_PAGE) && currentPage < MAX_PAGE) {
                show(currentPage + 1);
            } else if(e.getTextdraw().equals(PREV_PAGE) && currentPage > 0) {
                show(currentPage - 1);
            } else if(e.getTextdraw().equals(EXIT_BUTTON)) {
                hide();
            }
        });
    }

    public void show(int page) {
        currentPage = page;
        for(int i = page * ITEMS_PER_PAGE; i < (page + 1) * ITEMS_PER_PAGE; i++) {
            int prevPageTdIndex = i - ITEMS_PER_PAGE,
                nextPageTdIndex = i + ITEMS_PER_PAGE;
            if(prevPageTdIndex >= 0 && TEXTDRAWS[prevPageTdIndex].isShowed(player)) {
                TEXTDRAWS[prevPageTdIndex].hide(player);
            }
            if(nextPageTdIndex < TEXTDRAWS.length && TEXTDRAWS[nextPageTdIndex].isShowed(player)) {
                TEXTDRAWS[nextPageTdIndex].hide(player);
            }
            if(i < TEXTDRAWS.length)
                TEXTDRAWS[i].show(player);
        }
        if(!PREV_PAGE.isShowed(player)) {
            PREV_PAGE.show(player);
        }
        if(!NEXT_PAGE.isShowed(player)) {
            NEXT_PAGE.show(player);
        }
        if(!EXIT_BUTTON.isShowed(player))
            EXIT_BUTTON.show(player);
    }

    public void hide() {
        player.cancelSelectTextDraw();
        for(Textdraw td : TEXTDRAWS) {
            if(td.isShowed(player)) {
                td.hide(player);
            }
        }
        NEXT_PAGE.hide(player);
        PREV_PAGE.hide(player);
        EXIT_BUTTON.hide(player);
        eventManagerNode.cancelAll();
    }


    @FunctionalInterface
    public interface ColorPickHandler {
        void colorSelected(int color);
    }


}
