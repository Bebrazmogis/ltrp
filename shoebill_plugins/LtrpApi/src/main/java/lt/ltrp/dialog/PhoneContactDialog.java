package lt.ltrp.dialog;

import lt.ltrp.data.Color;
import lt.ltrp.ItemController;
import lt.ltrp.data.PhoneContact;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.21.
 */
public class PhoneContactDialog {

    protected static ListDialog create(LtrpPlayer p, EventManager eventManager, PhoneContact contact,
                                       PhonebookListDialog.CallContactHandler callContactHandler,
                                       PhonebookListDialog.SendSmsHandler sendSmsHandler,
                                       PhonebookListDialog.ContactDeleteHandler deleteHandler) {
        return ListDialog.create(p, eventManager)
                .caption(contact.getName() + " redagavimas")
                .buttonOk("Gerai")
                .buttonCancel("Atgal")
                .item("Skambinti", callOption -> {
                    if(callContactHandler != null)
                        callContactHandler.onSelectCallOption(callOption.getCurrentDialog(), contact);
                    //initiateCall(player, inventory, selectedContact.getNumber());
                })
                .item("Raðyti þinutæ", smsOption -> {
                    InputDialog.create(p, eventManager)
                            .caption("Nauja þinutë " + contact.getName())
                            .message("Áveskite þinutës tekstà:")
                            .buttonOk("Tæsti")
                            .buttonCancel("Atgal")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((textDialog, msgText) -> {
                                if(msgText.isEmpty()) {
                                    textDialog.show();
                                    p.sendErrorMessage("Þinutës tekstas negali bûti tuðèias");
                                } else {
                                    //sendSms(player, inventory, selectedContact.getNumber(), currentSms);
                                    if(sendSmsHandler != null)
                                        sendSmsHandler.onSendSms(smsOption.getCurrentDialog(), contact, msgText);
                                }

                            })
                            .build()
                            .show();
                })
                .item("Keisti numerá", changeNumberOption -> {
                    InputDialog.create(p, eventManager)
                            .caption("Naujas " + contact.getName() + " numeris")
                            .message("Áveskite kontakto " + contact.getName() + " naujà numerá."
                                    + "\nDabartinis numeris:" + contact.getNumber())
                            .buttonOk("Iðsaugoti")
                            .buttonCancel("Atðaukti")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((changeNumberDialog, newNumber) -> {
                                int number;
                                try {
                                    number = Integer.parseInt(newNumber);
                                } catch(NumberFormatException e) {
                                    p.sendErrorMessage("Numeris turi bûti sudarytas ið skaitmenu");
                                    changeNumberDialog.show();
                                    return;
                                }
                                contact.setNumber(number);
                                ItemController.get().getPhoneDao().update(contact);
                                p.sendMessage(Color.NEWS, "Kontakto \"" + contact.getName() + "\" numeris atnaujintas. Naujasis numeris " + contact.getName());
                                changeNumberOption.getCurrentDialog().show();
                            })
                            .build()
                            .show();
                })
                .item("Keisti vardà", changeNameOption -> {
                    InputDialog.create(p, eventManager)
                            .caption("Naujas " + contact.getNumber() + " vardas")
                            .message("Áveskite kontakto \"" + contact.getName() + "\" naujà vardà."
                                    + "\nDabartinis numeris:" + contact.getNumber())
                            .buttonOk("Iðsaugoti")
                            .buttonCancel("Atðaukti")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((changeNameDialog, newName) -> {
                                if(newName == null || newName.isEmpty()) {
                                    changeNameDialog.show();
                                    p.sendErrorMessage("Kontakto vardas negali bûti tuðèias");
                                } else {
                                    contact.setName(newName);
                                    ItemController.get().getPhoneDao().update(contact);
                                    p.sendMessage(Color.NEWS, "Kontakto vardas atnaujintas. Naujasis vardas \"" + newName + "\"");
                                    changeNameOption.getCurrentDialog().show();
                                }
                            })
                            .build()
                            .show();
                })
                .item("{FF0000}Paðalinti", removeOption -> {
                    MsgboxDialog.create(p, eventManager)
                            .caption("Svarbu.")
                            .message("Ar tikrai norite paðaliti kontaktà ið telefono atminties?"
                                    + "\n\nKontakto numeris:" + contact.getNumber()
                                    + "\nKontakto vardas: " + contact.getName()
                                    + "\n\nIðtrynus kontaktà jo sugràþinti nebeámanoma."
                                    + "\nAr tikrai norite tæsti?")
                            .buttonCancel("Ne")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .buttonOk("Taip")
                            .onClickOk(warningDialog -> {
                                if(deleteHandler != null)
                                    deleteHandler.onDeleteContact(removeOption.getCurrentDialog(), contact);
                                removeOption.getCurrentDialog().show();
                            })
                            .build()
                            .show();
                })
                .build();
    }

}
