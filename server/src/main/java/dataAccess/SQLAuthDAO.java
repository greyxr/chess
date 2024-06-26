package dataAccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearAuth() throws DataAccessException {
        String sql = "TRUNCATE auth";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(UUID authtoken) throws DataAccessException {
        if (authtoken == null) {
            throw new DataAccessException("Error: authtoken cannot be null");
        }
        String statement = "DELETE FROM auth WHERE authtoken = ?";
        executeUpdate(statement, authtoken.toString());

    }

    @Override
    public UUID createAuth(String username) throws DataAccessException {
        String statement = "INSERT INTO auth (authtoken, username) values (?, ?)";
        UUID authtoken = UUID.randomUUID();
        executeUpdate(statement, authtoken.toString(), username);
        return authtoken;
    }

    @Override
    public AuthData getAuth(UUID authtoken) throws DataAccessException {
        AuthData authData = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authtoken, username FROM auth WHERE authtoken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, String.valueOf(authtoken));
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        authData = new AuthData(UUID.fromString(rs.getString("authtoken")), rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return authData;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else {
                        if (param == null) ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `authtoken` varchar(256) NOT NULL,
              `username` TEXT NOT NULL,
              PRIMARY KEY (`authtoken`)
            )
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseHelper.setUpDatabase(createStatements);
    }
}
