package lt.ltrp.dialog;


import kotlin.Unit;
import lt.ltrp.data.PhoneContact;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.dialog.InputDialog;
import lt.maze.dialog.ListDialog;
import lt.maze.dialog.ListDialogItem;
import lt.maze.dialog.MsgBoxDialog;
import net.gtaun.shoebill.data.Color;
import net.gtaun.util.event.EventManager;

import static lt.ltrp.constant.LtrpColorKt.getNEWS;

/**
 * @author Bebras
 *         2016.02.21.
 */
public class PhoneContactDialog {

    protected static ListDialog create(LtrpPlayer p, EventManager eventManager, PhoneContact contact,
                                       PhonebookListDialog.CallContactHandler callContactHandler,
                                       PhonebookListDialog.SendSmsHandler sendSmsHandler,
                                       PhonebookListDialog.ContactDeleteHandler deleteHandler) {
        return ListDialog.Companion.create(p.getPlayer(), eventManager, (builder) -> {
            builder.caption(contact.getName() + " redagavimas");
            builder.buttonOk("Gerai");
            builder.buttonCancel("Atgal");
            builder.item(ListDialogItem.Companion.create(item -> {
                item.setItemText("Skambinti");
                item.selectHandler(item2 -> {
                    //initiateCall(player, inventory, selectedContact.getNumber());
                    if(callContactHandler != null)
                        callContactHandler.onSelectCallOption(item.getDialog(), contact);
                    return Unit.INSTANCE;
                });
                return Unit.INSTANCE;
            }));
            builder.item("Ra�yti �inut�", smsOption -> {
                InputDialog.Companion.create(p.getPlayer(), eventManager, inputBuilder -> {
                    inputBuilder.caption("Nauja �inut� " + contact.getName());
                    inputBuilder.body("�veskite �inut�s tekst�:");
                    inputBuilder.buttonOk("T�sti");
                    inputBuilder.buttonCancel("Atgal");
                    inputBuilder.onClickCancel(d -> {
                        d.show();
                        return Unit.INSTANCE;
                    });
                    inputBuilder.onClickOk((textDialog, msgText) -> {
                        if(msgText.isEmpty()) {
                            textDialog.show();
                            p.sendErrorMessage("�inut�s tekstas negali b�ti tu��ias");
                        } else {
                            //sendSms(player, inventory, selectedContact.getNumber(), currentSms);
                            if(sendSmsHandler != null)
                                sendSmsHandler.onSendSms(smsOption.getDialog(), contact, msgText);
                        }
                        return Unit.INSTANCE;
                    });
                    return Unit.INSTANCE;
                }).show();
                return Unit.INSTANCE;
            });
            builder.item("Keisti numer�", changeNumberOption -> {
                InputDialog.Companion.create(p.getPlayer(), eventManager, inputBuilder -> {
                    inputBuilder.caption("Naujas " + contact.getName() + " numeris");
                    inputBuilder.caption("�veskite kontakto " + contact.getName() + " nauj� numer�."
                                    + "\nDabartinis numeris:" + contact.getNumber());
                    inputBuilder.buttonOk("I�saugoti");
                    inputBuilder.buttonCancel("At�aukti");
                    inputBuilder.onClickCancel(d -> {
                        d.showParent();
                        return Unit.INSTANCE;
                    });
                    inputBuilder.onClickOk((changeNumberDialog, newNumber) -> {
                        int number;
                        try {
                            number = Integer.parseInt(newNumber);
                        } catch(NumberFormatException e) {
                            p.sendErrorMessage("Numeris turi b�ti sudarytas i� skaitmenu");
                            changeNumberDialog.show();
                            return Unit.INSTANCE;
                        }
                        contact.setNumber(number);
                        //ItemController.get().getPhoneDao().update(contact);
                        p.sendMessage(getNEWS(Color.Companion), "Kontakto \"" + contact.getName() + "\" numeris atnaujintas. Naujasis numeris " + contact.getName());
                        changeNumberOption.getDialog().show();
                        return Unit.INSTANCE;
                    });
                    return Unit.INSTANCE;
                }).show();
                return Unit.INSTANCE;
            });
            builder.item("Keisti vard�", changeNameOption -> {
                InputDialog.Companion.create(p.getPlayer(), eventManager, inputBuilder -> {
                    inputBuilder.caption("Naujas " + contact.getNumber() + " vardas");
                    inputBuilder.body("�veskite kontakto \"" + contact.getName() + "\" nauj� vard�."
                            + "\nDabartinis numeris:" + contact.getNumber());
                    inputBuilder.buttonOk("I�saugoti");
                    inputBuilder.buttonCancel("At�aukti");
                    inputBuilder.onClickCancel(d -> {
                        d.showParent();
                        return Unit.INSTANCE;
                    });
                    inputBuilder.onClickOk((changeNameDialog, newName) -> {
                        if(newName == null || newName.isEmpty()) {
                            changeNameDialog.show();
                            p.sendErrorMessage("Kontakto vardas negali b�ti tu��ias");
                        } else {
                            contact.setName(newName);
                            // ItemController.get().getPhoneDao().update(contact);
                            p.sendMessage(getNEWS(Color.Companion), "Kontakto vardas atnaujintas. Naujasis vardas \"" + newName + "\"");
                            changeNameOption.getDialog().show();
                        }
                        return Unit.INSTANCE;
                    });
                    return Unit.INSTANCE;
                });
                return Unit.INSTANCE;
            });
            builder .item("{FF0000}Pa�alinti", removeOption -> {
                MsgBoxDialog.Companion.create(p.getPlayer(), eventManager, msgboxBuilder -> {
                    msgboxBuilder.caption("Svarbu.");
                    msgboxBuilder.body("Ar tikrai norite pa�aliti kontakt� i� telefono atminties?"
                            + "\n\nKontakto numeris:" + contact.getNumber()
                            + "\nKontakto vardas: " + contact.getName()
                            + "\n\nI�trynus kontakt� jo sugr��inti nebe�manoma."
                            + "\nAr tikrai norite t�sti?");
                    msgboxBuilder.buttonCancel("Ne");
                    msgboxBuilder.onClickCancel(d -> {
                        d.showParent();
                        return Unit.INSTANCE;
                    });
                    msgboxBuilder.buttonOk("Taip");
                    msgboxBuilder.onClickOk(warningDialog -> {
                        if(deleteHandler != null)
                            deleteHandler.onDeleteContact(removeOption.getDialog(), contact);
                        removeOption.getDialog().show();
                        return Unit.INSTANCE;
                    });
                    return Unit.INSTANCE;
                });
                return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
        });
    }

}
