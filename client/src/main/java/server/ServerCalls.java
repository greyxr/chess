package server;

import com.google.gson.Gson;
import exceptions.ServerError;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;

public class ServerCalls {
    private static String url;

    ServerCalls(String url) {
        ServerCalls.url = url;
    }

    public ListGamesResponse getGames(UUID authtoken) throws ServerError {
        try {
            var method = "GET";
            var body = "";
            HttpURLConnection http = sendRequest(ServerCalls.url + "/game", method, body, authtoken);
            ListGamesResponse result = (ListGamesResponse) receiveResponse(http, ListGamesResponse.class);
            return  result;
        } catch (Exception e) {
           throw new ServerError(500, e.getMessage());
        }
    }

    public void logoutRequest(UUID authToken) throws ServerError {
        try {
            var method = "DELETE";
            String body = "";
            HttpURLConnection http = sendRequest(ServerCalls.url + "/session", method, body, authToken);
            Object result = receiveResponse(http, AuthData.class);
            // If object isn't an authtoken, it won't get here
        } catch (ServerError | URISyntaxException | IOException e) {
            throw new ServerError(500, e.getMessage());
        }
    }

    public AuthData loginRequest(UserData userData) throws ServerError {
        try {
            var method = "POST";
            UUID auth = null;
            HttpURLConnection http = sendRequest(ServerCalls.url + "/session", method, new Gson().toJson(userData), auth);
            Object result = receiveResponse(http, AuthData.class);
            // If object isn't an authtoken, it won't get here
            return (AuthData) result;
        } catch (ServerError | URISyntaxException | IOException e) {
            throw new ServerError(500, e.getMessage());
        }
    }

    public AuthData registerRequest(UserData userData) throws ServerError {
        try {
            var method = "POST";
            UUID auth = null;
            HttpURLConnection http = sendRequest(ServerCalls.url + "/user", method, new Gson().toJson(userData), auth);
            Object result = receiveResponse(http, AuthData.class);
            // If object isn't an authtoken, it won't get here
            return (AuthData) result;
        } catch (ServerError | URISyntaxException | IOException e) {
            throw new ServerError(500, e.getMessage());
        }
    }
        private static HttpURLConnection sendRequest(String url, String method, String body, UUID auth) throws URISyntaxException, IOException, IOException {
            URI uri = new URI(url);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod(method);
            if (auth != null) {
                http.setRequestProperty("Authorization", auth.toString());
            }
            writeRequestBody(body, http);
            http.connect();
            return http;
        }

        private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
            if (!body.isEmpty()) {
                http.setDoOutput(true);
                try (var outputStream = http.getOutputStream()) {
                    outputStream.write(body.getBytes());
                }
            }
        }

        private static <T> Object receiveResponse(HttpURLConnection http, Class<T> tclass) throws IOException, ServerError {
            var statusCode = http.getResponseCode();
            var statusMessage = http.getResponseMessage();
            if (statusCode == 200 || statusCode == 201) {
                return readResponseBody(http, tclass);
            } else {
                throw new ServerError(statusCode, statusMessage);
            }
        }

        private static <T> Object readResponseBody(HttpURLConnection http, Class<T> tClass) throws IOException {
            Object responseBody = "";
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                responseBody = new Gson().fromJson(inputStreamReader, tClass);
            }
            return responseBody;
        }

}
