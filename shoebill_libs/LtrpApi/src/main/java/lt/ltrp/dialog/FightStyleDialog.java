package lt.ltrp.dialog;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.dialog.ListDialog;
import lt.maze.dialog.ListDialogItem;
import net.gtaun.shoebill.data.Color;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.02.07.
 */
public class FightStyleDialog {

    private static final FightStyle[] fightStyles = new FightStyle[] {
            new FightStyle("Gatv�s kovos stilius", 7, 500),
            new FightStyle("Kun Fu kovos stilius", 6, 500),
            new FightStyle("Bokso kovos stilius", 5, 500)
    };

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        List<ListDialogItem> dialogItems = new ArrayList<>();
        int count = 1;
        for(FightStyle f : fightStyles) {
            ListDialogItem item = new ListDialogItem();
            item.setItemText(String.format("%d. %s - $%d", count++, f.name, f.price));
            item.setData(f);
            dialogItems.add(item);
        }
        return ListDialog.Companion.create(player.getPlayer(), eventManager, new Function1<ListDialog.ListDialogBuilder, Unit>() {
            @Override
            public Unit invoke(ListDialog.ListDialogBuilder listDialogBuilder) {
                listDialogBuilder.caption(() -> "Sporto sal� - Kovos stiliaus pamokos");
                listDialogBuilder.items(dialogItems);
                listDialogBuilder.buttonOk("Pirkti pam.");
                listDialogBuilder.buttonCancel("I�eiti");
                listDialogBuilder.onSelectItem((dialog, item) -> {
                    FightStyle style = (FightStyle) item.getData();
                    if (player.getPlayer().getMoney() < style.price) {
                        player.sendErrorMessage("Jums neu�tenka pinig� " + style.name + " pamokai. Pamokos kaina $" + style.price);
                    } else {
                        player.setBoxingStyle(style.styleId);
                        player.getPlayer().giveMoney(-style.price);
                        player.sendMessage(Color.PURPLE, style.name + " pamoka baigta, dabar jau mok�site i�naudoti �� stili�, pamoka kainavo $" + style.price);
                    }
                    return Unit.INSTANCE;
                });
                return Unit.INSTANCE;
            }
        });
    }


    private static class FightStyle {
        String name;
        int styleId,price;

        public FightStyle(String name, int styleId, int price) {
            this.name = name;
            this.styleId = styleId;
            this.price = price;
        }
    }

}
