package lt.ltrp.dialog;

import lt.ltrp.object.ItemPhone;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.impl.ItemPhoneImpl;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class PlayerPhoneSelectDialog extends ListDialog {

    public static PlayerPhoneSelectDialogBuilder create(LtrpPlayer player, EventManager eventManager) {
        return new PlayerPhoneSelectDialogBuilder(player, eventManager);
    }

    private SelectPhoneHandler selectPhoneHandler;

    protected PlayerPhoneSelectDialog(LtrpPlayer player, EventManager eventManager) {
        super(player, eventManager);
        setCaption(player.getName() + " telefonø sàraðas");
        setButtonOk("Pasirinkti");
        setButtonCancel("Uþdaryti");
    }

    public void setSelectPhoneHandler(SelectPhoneHandler selectPhoneHandler) {
        this.selectPhoneHandler = selectPhoneHandler;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }


    @Override
    public void show() {
        items.clear();

        for(ItemPhone phone : getPlayer().getInventory().getItems(ItemPhoneImpl.class)) {
            items.add(
                    ListDialogItem.create()
                            .itemText(String.format("%s\t%d", phone.getName(), phone.getPhonenumber()))
                            .data(phone)
                            .onSelect(i -> {
                                if (selectPhoneHandler != null) selectPhoneHandler.onSelectPhone(this, phone);
                            })
                            .build()
            );
        }

        super.show();
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        ItemPhone itemPhone = (ItemPhone)item.getData();
        if(selectPhoneHandler != null) selectPhoneHandler.onSelectPhone(this, itemPhone);
    }

    @FunctionalInterface
    public interface SelectPhoneHandler {
        void onSelectPhone(PlayerPhoneSelectDialog dialog, ItemPhone phone);
    }

    @SuppressWarnings("unchecked")
    public static class AbstractPlayerPhoneSelectDialogBuilder<DialogType extends PlayerPhoneSelectDialog, DialogBuilderType extends AbstractPlayerPhoneSelectDialogBuilder<DialogType, DialogBuilderType>>
        extends AbstractListDialogBuilder<DialogType, DialogBuilderType> {

        protected AbstractPlayerPhoneSelectDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType onSelectPhone(SelectPhoneHandler phoneHandler) {
            dialog.setSelectPhoneHandler(phoneHandler);
            return (DialogBuilderType)this;
        }
    }

    public static class PlayerPhoneSelectDialogBuilder extends AbstractPlayerPhoneSelectDialogBuilder<PlayerPhoneSelectDialog, PlayerPhoneSelectDialogBuilder> {

        protected PlayerPhoneSelectDialogBuilder(LtrpPlayer player, EventManager eventManager) {
            super(new PlayerPhoneSelectDialog(player, eventManager));
        }
    }
}
