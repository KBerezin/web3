package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;
import util.BasicConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) throws DBException {
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public List<BankClient> getAllClient() throws DBException {
        try {
            return getBankClientDAO().getAllBankClient();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean deleteClient(String name) throws DBException {
        try {
            BankClient client = getBankClientDAO().getClientByName(name);
            getBankClientDAO().deleteClient(client);
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return false;
    }

    public boolean addClient(BankClient client) throws DBException {

        try {
            if (getBankClientDAO().getClientIdByName(client.getName()) == -1) {
                getBankClientDAO().addClient(client);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) throws DBException {
        try {
            if (    value > 0
                    && getBankClientDAO().isBankClientExist(name)
                    && getBankClientDAO().validateClient(sender.getName(), sender.getPassword())
                    && getBankClientDAO().isClientHasSum(sender.getName(), value)
            ) {
                getBankClientDAO().sendMoney(sender, getClientByName(name), value);
                return true;
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return false;
    }

    public void cleanUp() throws DBException {
        try {
            BankClientDAO dao = getBankClientDAO();
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        try {
            BankClientDAO dao = getBankClientDAO();
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() throws SQLException {
        BasicConnectionPool connectionPool = new BasicConnectionPool(
                "jdbc:mysql://localhost:3306/db_example?useUnicode=true&serverTimezone=UTC",
                "root",
                "mysql",
                new ArrayList<>()
        );
        return connectionPool.getConnection();
    }

    private static BankClientDAO getBankClientDAO() throws SQLException {
        return new BankClientDAO(getMysqlConnection());
    }
}