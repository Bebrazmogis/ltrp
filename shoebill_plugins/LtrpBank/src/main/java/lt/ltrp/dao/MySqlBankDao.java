package lt.ltrp.dao;

import lt.ltrp.player.BankAccount;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.data.Location;

import java.sql.*;


/**
 * @author Bebras
 *         2016.03.02.
 */
public class MySqlBankDao implements BankDao {

    private Connection connection;

    public MySqlBankDao(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BankAccount getAccount(LtrpPlayer player) {
        String sql = "SELECT * FROM bank_accounts WHERE player_id = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                BankAccount account = new BankAccount(
                        resultSet.getInt("id"),
                        resultSet.getString("number"),
                        player.getUUID(),
                        resultSet.getInt("money"),
                        resultSet.getInt("deposit"),
                        resultSet.getInt("interest"),
                        resultSet.getTimestamp("deposit_date")
                );
                return account;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BankAccount getAccount(String number) {
        String sql = "SELECT * FROM bank_accounts WHERE number = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, number);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                BankAccount account = new BankAccount(
                        resultSet.getInt("id"),
                        resultSet.getString("number"),
                        resultSet.getInt("player_id"),
                        resultSet.getInt("money"),
                        resultSet.getInt("deposit"),
                        resultSet.getInt("interest"),
                        resultSet.getTimestamp("deposit_date")
                );
                return account;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertAccount(BankAccount account) {
        String sql = "INSERT INTO bank_accounts (number, player_id, money, deposit, interest) VALUES (?, ?, ?, ?, ?)";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, account.getNumber());
            stmt.setInt(2, account.getUserId());
            stmt.setInt(3, account.getMoney());
            stmt.setInt(4, account.getDeposit());
            stmt.setInt(5, account.getInterest());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next())
                account.setId(keys.getInt(1));
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAccount(BankAccount account) {
        String sql = "DELETE FROM bank_accounts WHERE id = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, account.getId());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAccount(BankAccount account) {
        String sql = "UPDATE bank_accounts SET number = ?, player_id = ?, money = ?, deposit = ?, deposit_date = ?, interest = ? WHERE id = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, account.getNumber());
            stmt.setInt(2, account.getUserId());
            stmt.setInt(3, account.getMoney());
            stmt.setInt(4, account.getDeposit());
            stmt.setTimestamp(5, account.getDepositTimestamp());
            stmt.setInt(6, account.getInterest());
            stmt.setInt(7, account.getId());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setInterest(int interest) {
        String sql = "UPDATE bank_properties SET `value` = ? WHERE `key` = 'interest'";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, Integer.toString(interest));
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getInterest() {
        String sql = "SELECT `value` FROM bank_properties WHERE `key` = 'interest'";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                return Integer.parseInt(resultSet.getString("value"));
            } else {
                Statement s = connection.createStatement();
                s.execute("INSERT INTO bank_properties (`key`, `value`) VALUES ('interest', '0')");
                s.close();
                return 0;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setNewAccountPrice(int price) {
        String sql = "UPDATE bank_properties SET `value` = ? WHERE `key` = 'account_price'";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, Integer.toString(price));
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNewAccountPrice() {
        String sql = "SELECT `value` FROM bank_properties WHERE `key` = 'account_price'";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                return Integer.parseInt(resultSet.getString("value"));
            } else {
                Statement s = connection.createStatement();
                s.execute("INSERT INTO bank_properties (`key`, `value`) VALUES ('account_price', '0')");
                s.close();
                return 0;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Location getBankLocation() {
        String sql = "SELECT `value` FROM bank_properties WHERE `key` = 'bank_location'";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                String s = resultSet.getString("value");
                String[] parts = s.split(" ");
                if(parts.length == 3) {
                    return new Location(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
                } else if(parts.length == 5) {
                    return new Location(
                            Float.parseFloat(parts[0]),
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])
                    );
                }
            } else {
                Statement s = connection.createStatement();
                s.execute("INSERT INTO bank_properties (`key`, `value`) VALUES ('bank_location', '295.772 1021.8 2123.61 0 0')");
                s.close();
                return null;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Location getPaycheckLocation() {
        String sql = "SELECT `value` FROM bank_properties WHERE `key` = 'paycheck_location'";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                String s = resultSet.getString("value");
                String[] parts = s.split(" ");
                if(parts.length == 3) {
                    return new Location(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
                } else if(parts.length == 5) {
                    return new Location(
                            Float.parseFloat(parts[0]),
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])
                    );
                }
            } else {
                Statement s = connection.createStatement();
                s.execute("INSERT INTO bank_properties (`key`, `value`) VALUES ('paycheck_location', '298.23 1021.78 2123.61 0 0')");
                s.close();
                return null;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void logWire(BankAccount from, BankAccount to, int amount) {
        String sql = "INSERT INTO bank_wire_trasnfer_log (from_account_id, to_account_id, amount, `date`) VALUES (?, ?, ?, ?)";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, from.getId());
            stmt.setInt(2, to.getId());
            stmt.setInt(3, amount);
            stmt.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
