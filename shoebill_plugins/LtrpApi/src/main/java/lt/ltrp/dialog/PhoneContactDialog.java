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
                .item("Ra�yti �inut�", smsOption -> {
                    InputDialog.create(p, eventManager)
                            .caption("Nauja �inut� " + contact.getName())
                            .message("�veskite �inut�s tekst�:")
                            .buttonOk("T�sti")
                            .buttonCancel("Atgal")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((textDialog, msgText) -> {
                                if(msgText.isEmpty()) {
                                    textDialog.show();
                                    p.sendErrorMessage("�inut�s tekstas negali b�ti tu��ias");
                                } else {
                                    //sendSms(player, inventory, selectedContact.getNumber(), currentSms);
                                    if(sendSmsHandler != null)
                                        sendSmsHandler.onSendSms(smsOption.getCurrentDialog(), contact, msgText);
                                }

                            })
                            .build()
                            .show();
                })
                .item("Keisti numer�", changeNumberOption -> {
                    InputDialog.create(p, eventManager)
                            .caption("Naujas " + contact.getName() + " numeris")
                            .message("�veskite kontakto " + contact.getName() + " nauj� numer�."
                                    + "\nDabartinis numeris:" + contact.getNumber())
                            .buttonOk("I�saugoti")
                            .buttonCancel("At�aukti")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((changeNumberDialog, newNumber) -> {
                                int number;
                                try {
                                    number = Integer.parseInt(newNumber);
                                } catch(NumberFormatException e) {
                                    p.sendErrorMessage("Numeris turi b�ti sudarytas i� skaitmenu");
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
                .item("Keisti vard�", changeNameOption -> {
                    InputDialog.create(p, eventManager)
                            .caption("Naujas " + contact.getNumber() + " vardas")
                            .message("�veskite kontakto \"" + contact.getName() + "\" nauj� vard�."
                                    + "\nDabartinis numeris:" + contact.getNumber())
                            .buttonOk("I�saugoti")
                            .buttonCancel("At�aukti")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((changeNameDialog, newName) -> {
                                if(newName == null || newName.isEmpty()) {
                                    changeNameDialog.show();
                                    p.sendErrorMessage("Kontakto vardas negali b�ti tu��ias");
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
                .item("{FF0000}Pa�alinti", removeOption -> {
                    MsgboxDialog.create(p, eventManager)
                            .caption("Svarbu.")
                            .message("Ar tikrai norite pa�aliti kontakt� i� telefono atminties?"
                                    + "\n\nKontakto numeris:" + contact.getNumber()
                                    + "\nKontakto vardas: " + contact.getName()
                                    + "\n\nI�trynus kontakt� jo sugr��inti nebe�manoma."
                                    + "\nAr tikrai norite t�sti?")
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
