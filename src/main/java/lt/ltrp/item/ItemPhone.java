package lt.ltrp.item;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.dao.PhoneDao;
import lt.ltrp.data.*;
import lt.ltrp.dialog.phone.PhonebookListDialog;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Property;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.dialog.*;
import net.gtaun.shoebill.object.Timer;

import java.lang.ref.WeakReference;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ItemPhone extends BasicItem {

    private static final int NUMBER_MIN = 86000000;
    private static final int NUMBER_MAX = 87000000;

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

    public ItemPhone(String name, int phonenumber) {
        super(name, ItemType.Phone, false);
        phones.add(new WeakReference<ItemPhone>(this));
        this.phonenumber = phonenumber;
    }

    public ItemPhone() {
        this("Telefonas", LtrpGamemode.getDao().getItemDao().generatePhonenumber());
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
        if(phonebook == null) {
            phonebook = LtrpGamemode.getDao().getPhoneDao().getPhonebook(phonenumber);
        }
        logger.debug("Phonebook:" + phonebook);
        PhonebookListDialog dialog = new PhonebookListDialog(player, ItemController.getInstance().getEventManager(), phonebook);
       // ListDialog dialog = ListDialog.create(player, ItemController.getInstance().getEventManager()).build();
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
                }).start();
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


    protected static int generateNumber(Connection con) throws SQLException {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        String sql = "SELECT number FROM items_phone";
        try (
                Statement stmt = con.createStatement();
                ) {
            ResultSet result =  stmt.executeQuery(sql);
            while(result.next()) {
                numbers.add(result.getInt(1));
            }
        }
        int phonenumber;
        while(numbers.contains((phonenumber = random.nextInt(NUMBER_MAX - NUMBER_MIN) + NUMBER_MIN)))
            continue;
        return phonenumber;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_phone SET `name` = ?, stackable = ?, number = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getPhonenumber());
        stmt.setInt(4, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_phone (`name`, stackable, number) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getPhonenumber());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_phone WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

    protected static ItemPhone getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_phone WHERE id = ?";
        ItemPhone item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new ItemPhone(result.getString("name"), result.getInt("number"));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
