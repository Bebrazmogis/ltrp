package lt.ltrp.item;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.dao.PhoneDao;
import lt.ltrp.data.Color;
import lt.ltrp.data.PhoneBook;
import lt.ltrp.data.PhoneCall;
import lt.ltrp.data.PhoneSms;
import lt.ltrp.item.event.PlayerCallNumberEvent;
import lt.ltrp.item.event.PlayerSendSmsEvent;
import lt.ltrp.item.phone.PhoneController;
import lt.ltrp.item.phone.dialog.PhonebookListDialog;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Property;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ItemPhone extends BasicItem {

    private static final int NUMBER_MIN = 86000000;
    private static final int NUMBER_MAX = 87000000;

    private static List<WeakReference<ItemPhone>> phones = new ArrayList<>();

    public static Collection<ItemPhone> get() {
        Collection<ItemPhone> c = new ArrayList<>();
        for(WeakReference<ItemPhone> phone : phones) {
            if(phone.get() != null) {
                c.add(phone.get());
            } else {
                phones.remove(phone);
            }
        }
        return c;
    }

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
    private PhoneBook phonebook;
    private PhoneCall phonecall;
    private boolean busy;

    public ItemPhone(int id, String name, EventManager eventManager, int phonenumber, PhoneBook phonebook) {
        super(id, name, eventManager, ItemType.Phone, false);
        phones.add(new WeakReference<ItemPhone>(this));
        this.phonenumber = phonenumber;
        if(phonebook == null) {
            this.phonebook = new PhoneBook(phonenumber);
        } else {
            this.phonebook = phonebook;
        }
    }


    public PhoneCall getPhonecall() {
        return phonecall;
    }

    public void setPhonecall(PhoneCall phonecall) {
        this.phonecall = phonecall;
    }

    public int getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(int phonenumber) {
        this.phonenumber = phonenumber;
    }

    public PhoneBook getPhoneBook() {
        return phonebook;
    }

    public boolean isBusy() {
        return busy || phonecall != null;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
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
        logger.debug("Phonebook:" + phonebook);
        PhonebookListDialog dialog = new PhonebookListDialog(player, getEventManager(), phonebook);
        dialog.setCallContactHandler((d, c) -> {
           initiateCall(player, inventory, c.getNumber());
        });

        dialog.setSendSmsHandler((d, c, s) -> {
           sendSms(player, inventory, c.getNumber(), s);
        });

        dialog.setDeleteHandler((d, contact) -> {
            LtrpGamemode.getDao().getPhoneDao().remove(contact);
            phonebook.remove(contact);
            player.sendMessage(Color.NEWS, "Kontaktas " + contact.getName() + " paðalintas");
        });
        dialog.show();
        return true;
    }


    public void initiateCall(LtrpPlayer player, Inventory inventory, int phonenumber) {
        // Kol kas leidþiam skambinti tik ið jo paties inventoriaus.
        if(player.getInventory() != inventory) {
            return;
        }
        getEventManager().dispatchEvent(new PlayerCallNumberEvent(player, this, phonenumber));
    }

    public void sendSms(LtrpPlayer player, Inventory inventory, int phonenumber, String messagetext) {
        //TODO log that message

        // Kol kas leidþiam raðyti tik ið jo paties inventoriaus.
        if(player.getInventory() != inventory) {
            return;
        }

        getEventManager().dispatchEvent(new PlayerSendSmsEvent(player, this, phonenumber, messagetext));
    }


    private void showSmsManagement(LtrpPlayer player, PhoneSms[] messages, Inventory inventory) {
        if(messages.length == 0) {
            MsgboxDialog.create(player, ItemController.getInstance().getEventManager())
                    .caption("Klaida.")
                    .message("SMS nëra!")
                    .buttonOk("Gerai")
                    .buttonCancel("")
                    .onClickOk(AbstractDialog::showParentDialog)
                    .build()
                    .show();
        }
        else {
            PageListDialog dialog = PageListDialog.create(player, ItemController.getInstance().getEventManager()).build();
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
                MsgboxDialog.create(player, ItemController.getInstance().getEventManager())
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




}
