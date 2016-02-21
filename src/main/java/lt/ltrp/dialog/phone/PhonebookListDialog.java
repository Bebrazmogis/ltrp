package lt.ltrp.dialog.phone;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.data.Color;
import lt.ltrp.data.PhoneContact;
import lt.ltrp.data.Phonebook;
import lt.ltrp.item.ItemController;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.21.
 */
public class PhonebookListDialog extends ListDialog {

    private Phonebook phonebook;
    private CallContactHandler callContactHandler;
    private SendSmsHandler sendSmsHandler;
    private ContactDeleteHandler deleteHandler;

    public PhonebookListDialog(LtrpPlayer player, EventManager eventManager, Phonebook phonebook) {
        super(player, eventManager);
        this.phonebook = phonebook;

        this.setCaption("Kontaktai");
        this.setButtonOk("Pasirinkti");
        this.setButtonCancel("Atgal");
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public void setSendSmsHandler(SendSmsHandler sendSmsHandler) {
        this.sendSmsHandler = sendSmsHandler;
    }

    public void setCallContactHandler(CallContactHandler callContactHandler) {
        this.callContactHandler = callContactHandler;
    }

    public void setDeleteHandler(ContactDeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    @Override
    public void show() {
        items.clear();
        for(PhoneContact contact : phonebook.getContacts()) {
            ListDialogItem item = new ListDialogItem();
            if(contact != null) {
                item.setItemText(contact.getName());
                item.setData(contact);
                item.setSelectHandler(h -> {
                    PhoneContactDialog.create(getPlayer(), eventManagerNode, contact, callContactHandler, sendSmsHandler, deleteHandler).show();
                });
            } else {
                item.setItemText("- Tu��ia - ");
                item.setSelectHandler(h -> {
                    // Pride�ti nauj� kontakt�
                    InputDialog.create(player, eventManagerNode)
                        .caption("Naujas kontaktas 1/2")
                        .buttonOk("T�sti")
                        .buttonCancel("Atgal")
                        .message("�veskite kontakto telefono numer�")
                        .onClickOk((contactNumberDialog, contactNumber) -> {
                            final int number;
                            try {
                                number = Integer.parseInt(contactNumber);
                            } catch (NumberFormatException e) {
                                getPlayer().sendErrorMessage("Numeris turi b�ti sudarytas i� skaitmenu");
                                contactNumberDialog.show();
                                return;
                            }
                            InputDialog.create(player, eventManagerNode)
                                    .caption("Naujas kontaktas 2/2")
                                    .buttonOk("I�saugoti")
                                    .buttonCancel("Atgal")
                                    .message("�veskite kontakto " + number + " vard�")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((contactNameDialog, contactName) -> {
                                        if (contactName != null && !contactName.isEmpty()) {
                                            PhoneContact newContact = LtrpGamemode.getDao().getPhoneDao().add(phonebook.getOwnerNumber(), number, contactName);
                                            phonebook.addContact(newContact);
                                            player.sendMessage(Color.NEWS, "Kontaktas \"" + contactName + "\" prid�tas � telefono kontakt� s�ra��");
                                        } else {
                                            contactNameDialog.show();
                                        }
                                    })
                                    .build()
                                    .show();
                        })
                        .build()
                        .show();
                });
            }
            items.add(item);
        }
        super.show();
    }


    @FunctionalInterface
    public interface CallContactHandler {
        void onSelectCallOption(ListDialog d, PhoneContact contact);
    }

    @FunctionalInterface
    public interface SendSmsHandler {
        void onSendSms(ListDialog d, PhoneContact c, String text);
    }

    @FunctionalInterface
    public interface ContactDeleteHandler {
        void onDeleteContact(ListDialog d, PhoneContact contact);
    }

}
