package dataAccess;

import exceptions.BadRequestException;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException, BadRequestException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String statement = "INSERT INTO users (username, password, email) values (?, ?, ?)";
        executeUpdate(statement, user.username(), hashedPassword, user.email());
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password FROM users WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                        user = new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return user;
    }

    @Override
    public void clearUsers() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE from users WHERE username = *";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    return;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else {
                        if (param == null) ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` TEXT NOT NULL,
              `password` TEXT NOT NULL,
              `email` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
