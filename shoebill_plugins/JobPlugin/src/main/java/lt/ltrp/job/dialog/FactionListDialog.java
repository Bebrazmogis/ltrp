package lt.ltrp.job.dialog;

import lt.ltrp.job.object.Faction;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.util.event.EventManager;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.03.03.
 */
public class FactionListDialog extends ListDialog {

    private Collection<Faction> factions;
    private SelectFactionHandler handler;

    protected FactionListDialog(LtrpPlayer player, EventManager eventManager, Collection<Faction> factions) {
        super(player, eventManager);
        this.factions = factions;
    }

    public void setSelectFactionHandler(SelectFactionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void show() {
        for(Faction f : factions) {
            items.add(new ListDialogItem(f, f.getName(), null));
        }
        super.show();
    }

    @Override
    public void onClickOk(ListDialogItem item) {
        if(handler != null)
            handler.onSelectFaction(this, (Faction)item.getData());
    }

    @FunctionalInterface
    public interface SelectFactionHandler {
        void onSelectFaction(FactionListDialog dialog, Faction faction);
    }

    public static FactionListDialogBuilder create(LtrpPlayer player, EventManager eventManager, Collection<Faction> factions) {
        return new FactionListDialogBuilder(player, eventManager, factions);
    }

    @SuppressWarnings("unchecked")
    public static abstract class AbstractFactionListDialogBuilder
            <DialogType extends FactionListDialog, DialogBuilderType extends AbstractFactionListDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractFactionListDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType onSelectFaction(SelectFactionHandler handler) {
            dialog.setSelectFactionHandler(handler);
            return (DialogBuilderType) this;
        }

    }

    public static class FactionListDialogBuilder extends AbstractFactionListDialogBuilder<FactionListDialog, FactionListDialogBuilder> {
        private FactionListDialogBuilder(LtrpPlayer player, EventManager parentEventManager, Collection<Faction> factions) {
            super(new FactionListDialog(player, parentEventManager, factions));
        }

    }
}
