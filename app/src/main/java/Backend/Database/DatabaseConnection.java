package Backend.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Backend.Player.PlayerInformation;

public class DatabaseConnection {

    public static final String DB_URL_ADDRESS = "jdbc:mysql://sql11.freemysqlhosting.net/sql11157079";
    public static final String DB_TABLE_NAME = "Information";
    public static final String DB_LOGIN = "sql11157079";
    public static final String SECRET_PASSWORD = ""; //ask me: daniilplyushchenko@gmail.com

    private String URLAddress;
    private String tableName;
    private String login;
    private String password;

    public DatabaseConnection(String URLAddress, String tableName, String login, String password) {
        this.URLAddress = URLAddress;
        this.tableName = tableName;
        this.login = login;
        this.password = password;
    }

    private Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(URLAddress, login, password);
    }

    private ResultSet executeQuery(Connection dbConnection, String query) throws SQLException {
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        statement.close();
        return resultSet;
    }

    public List<LeaderboardRecord> requestLeaderboard(int leaderbordSize) throws SQLException {
        Connection dbConnection = connectToDatabase();
        String query = "SELECT username, rating FROM " + tableName + " ORDER BY rating DESC LIMIT " + leaderbordSize;
        ResultSet resultSet = executeQuery(dbConnection, query);
        dbConnection.close();
        return buildLeaderboard(resultSet);
    }

    private List<LeaderboardRecord> buildLeaderboard(ResultSet resultSet) throws SQLException {
        List<LeaderboardRecord> result = new ArrayList<>();
        while (resultSet.next()){
            String username = resultSet.getString("username");
            int rating = resultSet.getInt("rating");
            result.add(new LeaderboardRecord(username, rating));
        }
        return result;
    }

    public List<DatabaseRecord> requestDatabaseRecords(List<PlayerInformation> playerInformations)
            throws InterruptedException, ExecutionException, SQLException {
        List<DatabaseRecord> result = new ArrayList<>();
        Connection dbConnection = connectToDatabase();
        insertAbsentPlayersIntoDatabase(dbConnection, playerInformations);
        for (PlayerInformation playerInformation : playerInformations){
            String query = "SELECT rating, played FROM" + tableName + " WHERE userID='" + playerInformation.getUserID() + "';";
            buildDatabaseRecord(playerInformations, executeQuery(dbConnection, query));
        }
        dbConnection.close();
        return result;
    }

    private List<DatabaseRecord> buildDatabaseRecord(
            List<PlayerInformation> playerInformations, ResultSet resultSet) throws SQLException {
        List<DatabaseRecord> results = new ArrayList<>();
        for (PlayerInformation playerInformation : playerInformations){
            String username = playerInformation.getUsername();
            String userID = playerInformation.getUserID();
            int rating = resultSet.getInt("rating");
            int played = resultSet.getInt("played");
            results.add(new DatabaseRecord(userID, username, rating, played));
        }
        return results;
    }

    private void insertAbsentPlayersIntoDatabase(Connection dbConnection, List<PlayerInformation> playerInformations) throws SQLException {
        for (PlayerInformation playerInformation : playerInformations){
            String query = "INSERT IGNORE INTO Information (userID, username) "
                    + "VALUES ('" + playerInformation.getUserID() + "', '" + playerInformation.getUsername() + "');";
            executeQuery(dbConnection, query);
        }
    }

}
