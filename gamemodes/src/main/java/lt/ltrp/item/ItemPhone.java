package lt.ltrp.item;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.dao.PhoneDao;
import lt.ltrp.data.*;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Property;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.dialog.*;
import net.gtaun.shoebill.object.Timer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ItemPhone extends BasicItem {

    private static List<WeakReference<ItemPhone>> phones = new ArrayList<>();

    public static ItemPhone get(int phoennumber) {
        for(WeakReference<ItemPhone> ref : phones) {
            if(ref.get() == null) {
                phones.remove(ref);
            } else if(ref.get().phonenumber == phoennumber) {
                return ref.get();
            }
        }
        return null;
    }


    private int phonenumber;
    private Phonebook phonebook;
    private Phonecall phonecall;

    private String currentSms = "";

    public ItemPhone(String name, int id, int phonenumber) {
        super(name, id, ItemType.Phone, false);
        phones.add(new WeakReference<ItemPhone>(this));
    }

    public Phonecall getPhonecall() {
        return phonecall;
    }

    public void setPhonecall(Phonecall phonecall) {
        this.phonecall = phonecall;
    }

    public int getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(int phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Phonebook getPhonebook() {
        return phonebook;
    }

    public void setPhonebook(Phonebook phonebook) {
        this.phonebook = phonebook;
    }

    @ItemUsageOption(name = "Perþiûrëti gautas SMS")
    public boolean showSmsInbox(LtrpPlayer player, Inventory inventory) {
        PhoneDao phoneDao = LtrpGamemode.getDao().getPhoneDao();
        if(phoneDao != null) {
            PhoneSms[] sms = phoneDao.getSmsByRecipient(this.phonenumber);
            showSmsManagement(player, sms, inventory);
            return true;
        }
        return false;
    }

    @ItemUsageOption(name = "Perþiûrëti iðsiøstas SMS")
    public boolean showSmsOutbox(LtrpPlayer player, Inventory inventory) {
        PhoneDao phoneDao = LtrpGamemode.getDao().getPhoneDao();
        if(phoneDao != null) {
            PhoneSms[] sms = phoneDao.getSmsBySender(this.phonenumber);
            showSmsManagement(player, sms, inventory);
            return true;
        }
        return false;
    }


    @ItemUsageOption(name = "Kontaktai")
    public boolean showPhoenbook(LtrpPlayer player, Inventory inventory) {
        // Should load the phonebook if it isn't loaded yet
        // Aka "lazy loading"
        ListDialog dialog = ListDialog.create(player, ItemController.getEventManager()).build();
        dialog.setCaption("Kontaktai");
        dialog.setButtonOk("Pasirinkti");
        dialog.setButtonCancel("Atgal");

        for(PhoneContact contact : phonebook.getContacts()) {
            ListDialogItem item = new ListDialogItem();
            if(contact != null) {
                item.setItemText(contact.getName());
                item.setData(contact);
            } else {
                item.setItemText("- Tuðèia - ");
            }
            dialog.addItem(item);
        }

        dialog.setClickOkHandler((contactListDialog, selectedItem) -> {
            if(selectedItem.getData() != null) {
                PhoneContact selectedContact = (PhoneContact)selectedItem.getData();
                ListDialog.create(player, ItemController.getEventManager())
                        .caption(selectedContact.getName() + " redagavimas")
                        .buttonOk("Gerai")
                        .buttonCancel("Atgal")
                        .item("Skambinti", callOption -> {
                            initiateCall(player, inventory, selectedContact.getNumber());
                        })
                        .item("Raðyti þinutæ", smsOption -> {
                            currentSms = "";
                            InputDialog.create(player, ItemController.getEventManager())
                                    .caption("Nauja þinutë " + selectedContact.getName())
                                    .message("Áveskite þinutës tekstà:")
                                    .buttonOk("Tæsti")
                                    .buttonCancel("Atgal")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((textDialog, msgText) -> {
                                        if(msgText != null && !msgText.isEmpty()) {
                                            currentSms += msgText;
                                        }
                                        ListDialog.create(player, ItemController.getEventManager())
                                                .caption("Nauja þinutë." + currentSms.length() + "/500")
                                                .buttonOk("Pasirinkti")
                                                .buttonCancel("Atðautki")
                                                .item("Siøsti", sendOption -> {
                                                    if(currentSms.isEmpty()) {
                                                        textDialog.show();
                                                        player.sendErrorMessage("Þinutës tekstas negali bûti tuðèias");
                                                    } else {
                                                        if(currentSms.length() > 500) {
                                                            currentSms = currentSms.substring(0, 501);
                                                        }
                                                        sendSms(player, inventory, selectedContact.getNumber(), currentSms);
                                                    }
                                                })
                                                .item("Papildyti tekstà", addTextOption -> {
                                                    textDialog.setMessage("Esamas tekstas:" + currentSms
                                                        + "\n\nDar galite panaudoti " + (500 - currentSms.length()) + " simbolius");
                                                    textDialog.show();
                                                })
                                                .build()
                                                .show();

                                    })
                                    .build()
                                    .show();
                        })
                        .item("Keisti numerá", changeNumberOption -> {
                            InputDialog.create(player, ItemController.getEventManager())
                                    .caption("Naujas " + selectedContact.getName() + " numeris")
                                    .message("Áveskite kontakto " + selectedContact.getName() + " naujà numerá."
                                        + "\nDabartinis numeris:" + selectedContact.getNumber())
                                    .buttonOk("Iðsaugoti")
                                    .buttonCancel("Atðaukti")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((changeNumberDialog, newNumber) -> {
                                        int number;
                                        try {
                                            number = Integer.parseInt(newNumber);
                                        } catch(NumberFormatException e) {
                                            player.sendErrorMessage("Numeris turi bûti sudarytas ið skaitmenu");
                                            changeNumberDialog.show();
                                            return;
                                        }
                                        selectedContact.setNumber(number);
                                        LtrpGamemode.getDao().getPhoneDao().update(selectedContact);
                                        changeNumberDialog.getParentDialog().show();
                                    })
                                    .build()
                                    .show();
                        })
                        .item("Keisti vardà", changeNameOption -> {
                            InputDialog.create(player, ItemController.getEventManager())
                                    .caption("Naujas " + selectedContact.getNumber() + " vardas")
                                    .message("Áveskite kontakto " + selectedContact.getName() + " naujà vardà."
                                            + "\nDabartinis numeris:" + selectedContact.getNumber())
                                    .buttonOk("Iðsaugoti")
                                    .buttonCancel("Atðaukti")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((changeNameDialog, newName) -> {
                                        if(newName == null || newName.isEmpty()) {
                                            changeNameDialog.show();
                                            player.sendErrorMessage("Kontakto vardas negali bûti tuðèias");
                                        } else {
                                            selectedContact.setName(newName);
                                            LtrpGamemode.getDao().getPhoneDao().update(selectedContact);
                                            changeNameDialog.getParentDialog().show();
                                        }
                                    })
                                    .build()
                                    .show();
                        })
                        .item("{FF0000}Paðalinti", removeOption -> {
                            MsgboxDialog.create(player, ItemController.getEventManager())
                                    .caption("Svarbu.")
                                    .message("Ar tikrai norite paðaliti kontaktà ið telefono atminties?"
                                            + "\n\nKontakto numeris:" + selectedContact.getNumber()
                                            + "\nKontakto vardas: " + selectedContact.getName()
                                            + "\n\nIðtrynus kontaktà jo sugràþinti nebeámanoma."
                                            + "\nAr tikrai norite tæsti?")
                                    .buttonCancel("Ne")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .buttonOk("Taip")
                                    .onClickOk(warningDialog -> {
                                        LtrpGamemode.getDao().getPhoneDao().remove(selectedContact);
                                        player.sendMessage(Color.NEWS, "Kontaktas " + selectedContact.getName() + " paðalintas");
                                        dialog.show();
                                    })
                                    .build()
                                    .show();
                        })
                        .build()
                        .show();
            } else {
                // Prideëti naujà kontaktà
                InputDialog.create(player, ItemController.getEventManager())
                        .caption("Naujas kontaktas 1/2")
                        .buttonOk("Tæsti")
                        .buttonCancel("Atgal")
                        .message("Áveskite kontakto telefono numerá")
                        .onClickOk((contactNumberDialog, contactNumber) -> {
                            final int number;
                            try {
                                number = Integer.parseInt(contactNumber);
                            } catch (NumberFormatException e) {
                                player.sendErrorMessage("Numeris turi bûti sudarytas ið skaitmenu");
                                contactNumberDialog.show();
                                return;
                            }
                            InputDialog.create(player, ItemController.getEventManager())
                                    .caption("Naujas kontaktas 2/2")
                                    .buttonOk("Iðsaugoti")
                                    .buttonCancel("Atgal")
                                    .message("Áveskite kontakto " + number + " vardà")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((contactNameDialog, contactName) -> {
                                        if (contactName != null && !contactName.isEmpty()) {
                                            PhoneContact contact = LtrpGamemode.getDao().getPhoneDao().add(this.phonenumber, number, contactName);
                                            phonebook.addContact(contact);
                                            player.sendMessage(Color.NEWS, contactName + " pridëtas á telefono kontaktø sàraðà");
                                        } else {
                                            contactNameDialog.show();
                                        }
                                    })
                                    .build()
                                    .show();
                        })
                        .build()
                        .show();
            }
        });
        return true;
    }


    public void initiateCall(LtrpPlayer player, Inventory inventory, int phonenumber) {
        // Kol kas leidþiam skambinti tik ið jo paties inventoriaus.
        if(player.getInventory() != inventory) {
            return;
        }

        if(isSpecialPhoneNumber(phonenumber)) {

        } else {
            final ItemPhone contactPhone = get(phonenumber);
            // First we check if a phone with that number exists
            if(contactPhone != null) {
                // Yay, phone exists. Let's start calling it

                final Phonecall phonecall = new Phonecall(player, this, contactPhone);
                contactPhone.setPhonecall(phonecall);
                this.setPhonecall(phonecall);
                Timer.create(2000, 4, new Timer.TimerCallback() {
                    @Override
                    public void onTick(int i) {
                        // Now we can start searching for its location
                        for(LtrpPlayer p : LtrpPlayer.get()) {
                            if(p.getInventory().contains(contactPhone)) {
                                p.sendStateMessage("kiðenëje skamba telefonas", 2.0f);
                                p.sendMessage(Color.NEWS, "Jums skambina. Norëdami atsiliepti naudokite /pickup, skambuèiui atmesti /hangup");
                                return;
                            }
                        }

                        for(LtrpVehicle vehicle : LtrpVehicle.get()) {
                            if(vehicle.getInventory().contains(contactPhone)) {
                                vehicle.sendActionMessage("Kaþkur automobilyje skamba telefonas...");
                            }
                        }

                        for(Property property : Property.get()) {
                            if(property.getInventory().contains(contactPhone)) {
                                property.sendStateMessage("Kaþkur netoli, girdisi telefono skambëjimas");
                            }
                        }
                    }
                    @Override
                    public void onStop() {
                        if(phonecall.getState() != Phonecall.PhonecallState.Talking) {
                            contactPhone.setPhonecall(null);
                            phonecall.getCallerPhone().setPhonecall(null);
                            player.sendMessage("Niekas neatsakë... Skambutis baigtas");
                        }
                    }
                });
            }
        }

    }

    public void sendSms(LtrpPlayer player, Inventory inventory, int phonenumber, String messagetext) {
        // Also log that message

        // Kol kas leidþiam raðyti tik ið jo paties inventoriaus.
        if(player.getInventory() != inventory) {
            return;
        }

        if(isSpecialPhoneNumber(phonenumber)) {
            // Abejoju kad apskritai reikës leisti SMS á spec. nuemerius...
        } else {
            player.sendMessage(Color.SMS_SENT, "SMS:" + messagetext);
            ItemPhone contactPhone = get(phonenumber);
            if(contactPhone != null) {
                // Now we can start searching for its location
                for(LtrpPlayer p : LtrpPlayer.get()) {
                    if(p.getInventory().contains(contactPhone)) {
                        p.sendStateMessage("kiðenëje suskamba telefonas", 2.0f);
                        String senderName = contactPhone.getPhonebook().contains(this.phonenumber) ?
                                contactPhone.getPhonebook().getContactName(this.phonenumber) :
                                Integer.toString(getPhonenumber());
                        p.sendMessage(Color.SMS_RECEIVED, "SMS: " + messagetext + ", siuntëjas: " + senderName);
                        p.playSound(1052);
                        return;
                    }
                }

                for(LtrpVehicle vehicle : LtrpVehicle.get()) {
                    if(vehicle.getInventory().contains(contactPhone)) {
                        vehicle.sendActionMessage("Kaþkur automobilyje suskamba telefonas...");
                    }
                }

                for(Property property : Property.get()) {
                    if(property.getInventory().contains(contactPhone)) {
                        property.sendStateMessage("Kaþkur netoli, suskamba telefonas");
                    }
                }
            }
        }
    }

    public static boolean isSpecialPhoneNumber(int phonenumber) {
        return true;
    }

    private void showSmsManagement(LtrpPlayer player, PhoneSms[] messages, Inventory inventory) {
        PageListDialog dialog = PageListDialog.create(player, ItemController.getEventManager()).build();
        for(PhoneSms msg : messages) {
            ListDialogItem dialogItem = new ListDialogItem();
            dialogItem.setData(msg);
            String messageShortText = msg.getText().length() > 30 ? msg.getText().substring(0, 30) + "..." : msg.getText();
            dialogItem.setItemText(msg.getDate().toString() + " " + messageShortText);

            dialog.addItem(dialogItem);
        }

        String caption = this.phonenumber == messages[0].getSenderNumber() ? "Iðsiøstos þinutës" : "Gautos þinutës";
        dialog.setCaption(caption);
        dialog.setItemsPerPage(20);
        dialog.setButtonOk("Perþiûrëti");
        dialog.setButtonCancel("Atgal");
        dialog.setNextPageItemText("Sekantis puslapis");
        dialog.setPrevPageItemText("Praeitas puslapis");
        dialog.setClickOkHandler((smsDialog, selectedItem) -> {
            PhoneSms msg = (PhoneSms)selectedItem.getData();
            MsgboxDialog.create(player, ItemController.getEventManager())
                    .caption("Þinutë")
                    .message("Data:" + msg.getDate().toString()
                            + "\n" + (this.phonenumber == messages[0].getSenderNumber() ?
                                    "Kam: " +  (phonebook.contains(msg.getRecipientNumber()) ? phonebook.getContactName(msg.getRecipientNumber()) : msg.getRecipientNumber()) :
                                    "Nuo: " + (phonebook.contains(msg.getSenderNumber()) ? phonebook.getContactName(msg.getSenderNumber()) : msg.getSenderNumber()))
                            + "\n\n" + msg.getText())
                    .buttonOk("Atgal")
                    .onClickOk(AbstractDialog::showParentDialog)
                    .build()
                    .show();

        });
    }
}
