package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearGames() throws DataAccessException {
        String sql = "TRUNCATE games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", sql, e.getMessage()));
        }
    }

    private GameData readGame(ResultSet resultSet) throws SQLException {
        int gameID = resultSet.getInt("game_id");
        String whiteUsername = resultSet.getString("white_username");
        String blackUsername = resultSet.getString("black_username");
        String gameName = resultSet.getString("game_name");
        ChessGame game = new Gson().fromJson(resultSet.getString("chess_game"), ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        Collection<GameData> result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData result = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games WHERE game_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result = readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void insertUser(int gameID, String username, String color) throws DataAccessException {
        String userColor = color.equalsIgnoreCase("white") ? "white_username" : "black_username";
        String sql = "UPDATE games SET " + userColor + " = ? WHERE game_id = ?";
        executeUpdate(sql, username, gameID);

    }

    @Override
    public int insertGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO games (game_id, game_name, white_username, black_username, chess_game) values (?, ?, ?, ?, ?)";
        ChessGame newGame = new ChessGame();
//        int gameId = getBiggestGameId();
//        gameId = gameId == -1 ? 0 : gameId;
        newGame.getBoard().resetBoard();
        return executeUpdate(statement, null, gameName, null, null, new Gson().toJson(newGame));
    }

    @Override
    public int saveGame(int gameId, ChessGame game) throws DataAccessException {
        String sql = "UPDATE games SET chess_game = ? WHERE game_id = ?";
        return executeUpdate(sql, new Gson().toJson(game), gameId);
    }

    @Override
    public int getBiggestGameId() throws DataAccessException {
        String sql = "SELECT max(game_id) FROM games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs == null) {
                        return -1;
                    }
                    rs.first();
                    return rs.getInt(1);

                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
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

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `game_id` int NOT NULL AUTO_INCREMENT,
              `game_name` TEXT NOT NULL,
              `white_username` TEXT DEFAULT NULL,
              `black_username` TEXT DEFAULT NULL,
              `chess_game` TEXT NOT NULL,
              PRIMARY KEY (`game_id`)
            )
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
