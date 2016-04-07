package lt.ltrp.item.phone.dialog;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.data.Color;
import lt.ltrp.item.phone.PhoneBook;
import lt.ltrp.item.phone.PhoneContact;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.21.
 */
public class PhonebookListDialog extends ListDialog {

    private PhoneBook phonebook;
    private CallContactHandler callContactHandler;
    private SendSmsHandler sendSmsHandler;
    private ContactDeleteHandler deleteHandler;

    public PhonebookListDialog(LtrpPlayer player, EventManager eventManager, PhoneBook phonebook) {
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
                item.setItemText("- Tuðèia - ");
                item.setSelectHandler(h -> {
                    // Prideëti naujà kontaktà
                    InputDialog.create(player, eventManagerNode)
                        .caption("Naujas kontaktas 1/2")
                        .buttonOk("Tæsti")
                        .buttonCancel("Atgal")
                        .message("Áveskite kontakto telefono numerá")
                        .onClickOk((contactNumberDialog, contactNumber) -> {
                            final int number;
                            try {
                                number = Integer.parseInt(contactNumber);
                            } catch (NumberFormatException e) {
                                getPlayer().sendErrorMessage("Numeris turi bûti sudarytas ið skaitmenu");
                                contactNumberDialog.show();
                                return;
                            }
                            InputDialog.create(player, eventManagerNode)
                                    .caption("Naujas kontaktas 2/2")
                                    .buttonOk("Iðsaugoti")
                                    .buttonCancel("Atgal")
                                    .message("Áveskite kontakto " + number + " vardà")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((contactNameDialog, contactName) -> {
                                        if (contactName != null && !contactName.isEmpty()) {
                                            PhoneContact newContact = LtrpGamemode.getDao().getPhoneDao().add(phonebook.getOwnerNumber(), number, contactName);
                                            phonebook.addContact(newContact);
                                            player.sendMessage(Color.NEWS, "Kontaktas \"" + contactName + "\" pridëtas á telefono kontaktø sàraðà");
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
