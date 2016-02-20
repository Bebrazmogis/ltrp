package lt.ltrp.dao.impl;

import lt.ltrp.Util.Sql;
import lt.ltrp.dao.PhoneDao;
import lt.ltrp.data.PhoneContact;
import lt.ltrp.data.PhoneSms;
import lt.ltrp.data.Phonebook;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

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
                sms.setId(keys.getInt(0));
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
    public Phonebook getPhonebook(int phonenumber) {
        Phonebook phonebook = null;
        String sql = "SELECT * FROM phone_contacts WHERE number = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, phonenumber);
            ResultSet result = stmt.executeQuery();

            while(result.next()) {
                if(phonebook == null) {
                    phonebook = new Phonebook(result.getInt("number"));
                }
                phonebook.addContact(resultToPhoneContact(result));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return phonebook;
    }

    @Override
    public PhoneContact add(int ownernumber, int contactnumber, String name) {
        PhoneContact contact = null;
        String sql = "INSERT INTO phone_contacts (number, contact_number, `name`, entry_date) VALUES(?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            Date date = Sql.convert(new GregorianCalendar().getTime());
            stmt.setInt(1, ownernumber);
            stmt.setInt(2, contactnumber);
            stmt.setString(3, name);
            stmt.setDate(4, date);
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()) {
                contact = new PhoneContact(keys.getInt(0), contactnumber, name, date);
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
            stmt.setDate(3, Sql.convert(contact.getDate()));
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
    public void logConversation(int phonenumberfrom, int phonenumberto, String text) {
        String sql = "INSERT INTO phone_conversation_logs (from_number, to_number, `text`, `date`) VALUES (?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, phonenumberfrom);
            stmt.setInt(2, phonenumberto);
            stmt.setString(3, text);
            stmt.setDate(4, Sql.convert(new GregorianCalendar().getTime()));
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
        return new PhoneContact(set.getInt("id"), set.getInt("contact_number"), set.getString("name"), set.getDate("entry_date"));
    }
}
