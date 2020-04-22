package dao;

import model.BankClient;
import util.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankClientDAO {
    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        return JDBCUtil.select(connection, "SELECT * FROM bank_client",
                resultSet -> {
                    List<BankClient> allClients = Collections.synchronizedList(new ArrayList<>());
                    while (resultSet.next()) {
                        allClients.add(clientGenerator(resultSet));
                    }
                    return allClients;
                });
    }

    public boolean validateClient(String name, String password) throws SQLException {
        final BankClient client = getClientByName(name);
        if (client != null) {
            return client.getPassword().equals(password);
        }
        return false;
    }

    public boolean validateClient(BankClient bankClient, String password) throws SQLException {
        if (bankClient != null) {
            return bankClient.getPassword().equals(password);
        }
        return false;
    }

    public boolean isBankClientExist (String name) throws SQLException {
        final BankClient clientByName = getClientByName(name);
        return clientByName != null;
    }

    private void updateClientMoney(String name, String password, Long transactValue) throws SQLException {
        BankClient bankClient = getClientByName(name);
        if (validateClient(bankClient, password)) {
            JDBCUtil.update(connection, "UPDATE bank_client SET money=? WHERE id=?;",
                    bankClient.getMoney() + transactValue, bankClient.getId());
        }
    }

    public void sendMoney(BankClient sender, BankClient receiver, Long transactValue) throws SQLException {
        connection.setAutoCommit(false);
        try {
            updateClientMoney(sender.getName(), sender.getPassword(), -transactValue);
            updateClientMoney(receiver.getName(), receiver.getPassword(), transactValue);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
        }
        connection.setAutoCommit(true);
    }

    public BankClient getClientById(long id) throws SQLException {
        return JDBCUtil.select(connection, "SELECT * FROM bank_client WHERE id=?",
                resultSet -> {
                    if (resultSet.next()) {
                        return clientGenerator(resultSet);
                    }
                    return null;
                }, id);
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        if (getClientByName(name) == null) {
            return false;
        }
        return getClientByName(name).getMoney() - expectedSum >= 0;
    }

    public long getClientIdByName(String name) throws SQLException {
        return JDBCUtil.select(connection, "SELECT id FROM bank_client WHERE name=?",
                resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getLong(1);
                    }
                    return -1L;
                }, name);
    }

    public BankClient getClientByName(String name) throws SQLException {
        if (getClientIdByName(name) <= 0) {
            return null;
        }
        return getClientById(getClientIdByName(name));
    }

    public void addClient(BankClient client) throws SQLException {
        JDBCUtil.update(connection, "INSERT INTO bank_client (name, password, money) VALUES (?, ?, ?);",
                client.getName(), client.getPassword(), client.getMoney());
    }

    public void deleteClient(BankClient client) throws SQLException {
        if (getClientIdByName(client.getName()) != -1) {
            JDBCUtil.update(connection, "DELETE FROM bank_client WHERE id=?;", client.getId());
        }
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(
                "create table if not exists bank_client (id bigint auto_increment, name varchar(256), " +
                        "password varchar(256), money bigint, primary key (id))"
        );
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }

    private BankClient clientGenerator(ResultSet resultSet) throws SQLException {
        return new BankClient(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getLong(4));
    }
}
