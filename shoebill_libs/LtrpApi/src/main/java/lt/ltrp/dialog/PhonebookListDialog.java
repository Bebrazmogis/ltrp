package lt.ltrp.dialog;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import lt.ltrp.data.PhoneBook;
import lt.ltrp.data.PhoneContact;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.dialog.InputDialog;
import lt.maze.dialog.ListDialog;
import lt.maze.dialog.ListDialogItem;
import net.gtaun.shoebill.data.Color;
import net.gtaun.util.event.EventManager;

import static lt.ltrp.constant.LtrpColorKt.getNEWS;

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

        this.setTitle("Kontaktai");
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
        getItems().clear();
        for(PhoneContact contact : phonebook.getContacts()) {
            ListDialogItem item = new ListDialogItem();
            if(contact != null) {
                item.setItemText(contact.getName());
                item.setData(contact);
                item.selectHandler(h -> {
                    PhoneContactDialog.create(getPlayer(), getEventNode(), contact, callContactHandler, sendSmsHandler, deleteHandler).show();
                    return Unit.INSTANCE;
                });
            } else {
                item.setItemText("- Tuðèia - ");
                item.selectHandler(h -> {
                    // Prideëti naujà kontaktà
                    InputDialog.Companion.create(getPlayer(), getEventNode(), new Function1<InputDialog.InputDialogBuilder, Unit>() {
                        @Override
                        public Unit invoke(InputDialog.InputDialogBuilder inputDialogBuilder) {
                            inputDialogBuilder.caption("Naujas kontaktas 1/2");
                            inputDialogBuilder.buttonOk("Tæsti");
                            inputDialogBuilder.buttonCancel("Atgal");
                            inputDialogBuilder.body("Áveskite kontakto telefono numerá");
                            inputDialogBuilder.onClickOk((contactNumberDialog, contactNumber) -> {
                                final int number;
                                try {
                                    number = Integer.parseInt(contactNumber);
                                } catch (NumberFormatException e) {
                                    getPlayer().sendErrorMessage("Numeris turi bûti sudarytas ið skaitmenu");
                                    contactNumberDialog.show();
                                    return Unit.INSTANCE;
                                }
                                InputDialog.Companion.create(getPlayer(), getEventNode(), new Function1<InputDialog.InputDialogBuilder, Unit>() {
                                    @Override
                                    public Unit invoke(InputDialog.InputDialogBuilder inputDialogBuilder) {
                                        inputDialogBuilder.caption("Naujas kontaktas 2/2");
                                        inputDialogBuilder.buttonOk("Iðsaugoti");
                                        inputDialogBuilder.buttonCancel("Atgal");
                                        inputDialogBuilder.body("Áveskite kontakto " + number + " vardà");
                                        inputDialogBuilder.onClickCancel(abstractDialog -> {
                                            abstractDialog.showParent();
                                            return Unit.INSTANCE;
                                        });
                                        inputDialogBuilder.onClickOk((contactNameDialog, contactName) -> {
                                            if (contactName != null && !contactName.isEmpty()) {
                                                //PhoneContact newContact = ItemController.get().getPhoneDao().add(phonebook.getOwnerNumber(), number, contactName);
                                                //phonebook.addContact(newContact);
                                                // TODO
                                                getPlayer().sendMessage(getNEWS(Color.Companion), "Kontaktas \"" + contactName + "\" pridëtas á telefono kontaktø sàraðà");
                                            } else {
                                                contactNameDialog.show();
                                            }
                                            return Unit.INSTANCE;
                                        });
                                        return Unit.INSTANCE;
                                    }
                                }).show();
                                return Unit.INSTANCE;
                                    });
                            return Unit.INSTANCE;
                        }
                    }).show();
                    return Unit.INSTANCE;
                });
            }
            getItems().add(item);
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
