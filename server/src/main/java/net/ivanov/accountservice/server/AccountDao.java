package net.ivanov.accountservice.server;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


public class AccountDao {
    private static final String PROPERTIES_LINK = "settings.properties";
    private static Properties properties;
    static final String GETALL_SQL = "select id, balance from account";
    static final String SAVE_SQL = "insert into account(id, balance) values (?,?) "
                                   + "on DUPLICATE KEY UPDATE balance=?";
    private Connection connection;

    public AccountDao() throws Exception {
        properties = new Properties();
        InputStream propertiesStream = RequestHandler.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_LINK);
        
        properties.load(propertiesStream);
        propertiesStream.close();
        connectDb();
    }

    private void connectDb() throws Exception {
        String dbDriver = properties.getProperty("db.driver");
        String dbUrl = properties.getProperty("db.url");
        String dbUser = properties.getProperty("db.user");
        String dbPassword = properties.getProperty("db.password");
        
        Class.forName(dbDriver);
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public Collection<Account> getAccounts() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            
            ResultSet rs = stmt.executeQuery(GETALL_SQL);
            List<Account> data = new LinkedList<>();
            
            int id = 0;
            long balance = 0L;
            
            while (rs.next()) {
                id = rs.getInt("id");
                balance = rs.getLong("balance");
                Account account = new Account(id, balance);
                
                data.add(account);
            }
            
            return data;
        }
    }

    public void save(int id, long balance) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SAVE_SQL)) {
            
            ps.setInt(1, id);
            ps.setLong(2, balance);
            ps.setLong(3, balance);
            ps.executeUpdate();
        }
    }
}
