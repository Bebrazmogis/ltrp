package lt.ltrp.item;

import lt.ltrp.common.data.Color;
import lt.ltrp.item.constant.ItemType;
import lt.ltrp.item.dao.PhoneDao;
import lt.ltrp.item.data.PhoneBook;
import lt.ltrp.item.data.PhoneCall;
import lt.ltrp.item.data.PhoneSms;
import lt.ltrp.item.dialog.PhonebookListDialog;
import lt.ltrp.item.event.PlayerCallNumberEvent;
import lt.ltrp.item.event.PlayerSendSmsEvent;
import lt.ltrp.item.object.Inventory;
import lt.ltrp.item.object.ItemPhone;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ItemPhoneImpl extends BasicItem implements ItemPhone {


    private static List<ItemPhoneImpl> phones = new ArrayList<>();

    public static List<ItemPhone> get() {
        ArrayList<ItemPhone> p = new ArrayList<>();
        phones.forEach(ph -> p.add((ItemPhone)ph));
        return p;
    }

    public static ItemPhone get(int phoennumber) {
        Optional<ItemPhoneImpl> op = phones.stream().filter(p -> p.phonenumber == phoennumber).findFirst();
        return op.isPresent() ? op.get() : null;
    }


    private int phonenumber;
    private PhoneBook phonebook;
    private PhoneCall phonecall;
    private boolean busy;

    public ItemPhoneImpl(int id, String name, EventManager eventManager, int phonenumber, PhoneBook phonebook) {
        super(id, name, eventManager, ItemType.Phone, false);
        phones.add(this);
        this.phonenumber = phonenumber;
        if(phonebook == null) {
            this.phonebook = new PhoneBook(phonenumber);
        } else {
            this.phonebook = phonebook;
        }
    }

    public ItemPhoneImpl(EventManager eventManager, int phonenumber) {
        this(0, Integer.toString(phonenumber), eventManager, phonenumber, null);
    }

    @Override
    public void destroy() {
        phones.remove(this);
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
        PhoneDao phoneDao = ItemController.get().getPhoneDao();
        if(phoneDao != null) {
            PhoneSms[] sms = phoneDao.getSmsByRecipient(this.phonenumber);
            showSmsManagement(player, sms, inventory);
            return true;
        }
        return false;
    }

    @ItemUsageOption(name = "Perþiûrëti iðsiøstas SMS")
    public boolean showSmsOutbox(LtrpPlayer player, Inventory inventory) {
        PhoneDao phoneDao = ItemController.get().getPhoneDao();
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
            ItemController.get().getPhoneDao().remove(contact);
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
        // Kol kas leidþiam raðyti tik ið jo paties inventoriaus.
        if(player.getInventory() != inventory) {
            return;
        }

        getEventManager().dispatchEvent(new PlayerSendSmsEvent(player, this, phonenumber, messagetext));
    }


    private void showSmsManagement(LtrpPlayer player, PhoneSms[] messages, Inventory inventory) {
        if(messages.length == 0) {
            MsgboxDialog.create(player, getEventManager())
                    .caption("Klaida.")
                    .message("SMS nëra!")
                    .buttonOk("Gerai")
                    .buttonCancel("")
                    .onClickOk(AbstractDialog::showParentDialog)
                    .build()
                    .show();
        }
        else {
            PageListDialog dialog = PageListDialog.create(player, getEventManager()).build();
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
                MsgboxDialog.create(player, getEventManager())
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
