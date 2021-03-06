package lt.ltrp.player.vehicle.dao.impl;

import lt.ltrp.dao.PhoneDao;
import lt.ltrp.data.PhoneBook;
import lt.ltrp.data.PhoneContact;
import lt.ltrp.data.PhoneSms;
import lt.ltrp.object.ItemPhone;
import lt.ltrp.util.Sql;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Bebras
 *         2015.11.30.
 *
 * A MySql implementation of the PhoneDao interface
 */
public class SqlPhoneDaoImpl implements PhoneDao {

    private DataSource dataSource;

    public SqlPhoneDaoImpl(DataSource ds) {
        this.dataSource = ds;
    }


    @Override
    public void addSms(PhoneSms sms) {
        String sql = "INSERT INTO phone_sms (sender_number, recipient_number, `date`, `text`, `read`) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            stmt.setInt(1, sms.getSenderNumber());
            stmt.setInt(2, sms.getRecipientNumber());
            stmt.setDate(3, Sql.convert(sms.getDate()));
            stmt.setString(4, sms.getText());
            stmt.setInt(5, sms.isRead() ? 1 : 0);
            ResultSet keys = stmt.executeQuery();
            if(keys.next()) {
                sms.setId(keys.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PhoneSms[] getSmsBySender(int phonenumber) {
        return getSmsByColumn(phonenumber, "sender_number");
    }

    @Override
    public PhoneSms[] getSmsByRecipient(int phonenumber) {
        return getSmsByColumn(phonenumber, "recipient_number");
    }

    private PhoneSms[] getSmsByColumn(int phonenumber, String columnname) {
        String sql = "SELECT * FROM phone_sms WHERE ? = ?";
        List<PhoneSms> messages = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, columnname);
            stmt.setInt(2, phonenumber);
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                messages.add(resultToPhoneSms(result));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return messages.toArray(new PhoneSms[0]);
    }

    @Override
    public void update(PhoneSms sms) {
        String sql = "UPDATE phone_sms SET sender_number = ?, recipient_number = ?, `date` = ?, `text` = ?, `read` = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, sms.getSenderNumber());
            stmt.setInt(2, sms.getRecipientNumber());
            stmt.setDate(3, Sql.convert(sms.getDate()));
            stmt.setString(4, sms.getText());
            stmt.setBoolean(5, sms.isRead());
            stmt.setInt(6, sms.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PhoneBook getPhonebook(int phonenumber) {
        PhoneBook phonebook = null;
        String sql = "SELECT * FROM phone_contacts WHERE number = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, phonenumber);
            ResultSet result = stmt.executeQuery();

            while(result.next()) {
                if(phonebook == null) {
                    phonebook = new PhoneBook(result.getInt("number"));
                }
                phonebook.addContact(resultToPhoneContact(result));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return phonebook == null ? new PhoneBook(phonenumber) : phonebook;
    }

    @Override
    public PhoneContact add(int ownernumber, int contactnumber, String name) {
        PhoneContact contact = null;
        String sql = "INSERT INTO phone_contacts (number, contact_number, `name`, entry_date) VALUES(?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
            stmt.setInt(1, ownernumber);
            stmt.setInt(2, contactnumber);
            stmt.setString(3, name);
            stmt.setTimestamp(4, timestamp);
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()) {
                contact = new PhoneContact(keys.getInt(1), contactnumber, name, timestamp);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    @Override
    public void update(PhoneContact contact) {
        String sql = "UPDATE phone_contacts SET contact_number = ?, `name` = ?, entry_date = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, contact.getNumber());
            stmt.setString(2, contact.getName());
            stmt.setTimestamp(3, contact.getDate());
            stmt.setInt(4, contact.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(PhoneContact contact) {
        String sql = "DELETE FROM phone_contacts WHERE id = ? LIMIT 1";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, contact.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int generateNumber() {
        String sql = "SELECT COUNT(item_id) FROM items_phone WHERE number = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            Random random = new Random();
            int number;
            while(true) {
                number = random.nextInt(ItemPhone.NUMBER_MAX - ItemPhone.NUMBER_MIN) + ItemPhone.NUMBER_MIN;
                stmt.setInt(1, number);
                ResultSet r = stmt.executeQuery();
                if(!r.next() || r.getInt(1) == 0) {
                    return number;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void logConversation(int phonenumberfrom, int phonenumberto, String text) {
        String sql = "INSERT INTO phone_conversation_logs (from_number, to_number, `text`, `date`) VALUES (?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, phonenumberfrom);
            stmt.setInt(2, phonenumberto);
            stmt.setString(3, text);
            stmt.setDate(4, new Date(new java.util.Date().getTime()));
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


    private PhoneSms resultToPhoneSms(ResultSet set) throws SQLException {
        PhoneSms sms = new PhoneSms(set.getInt("id"), set.getInt("sender_number"), set.getInt("recipient_number"), set.getDate("date"), set.getString("text"), set.getBoolean("read"));
        return sms;
    }

    private PhoneContact resultToPhoneContact(ResultSet set) throws SQLException {
        return new PhoneContact(set.getInt("id"), set.getInt("contact_number"), set.getString("name"), set.getTimestamp("entry_date"));
    }
}
