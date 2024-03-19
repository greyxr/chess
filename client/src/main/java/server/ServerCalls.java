package server;

import com.google.gson.Gson;
import exceptions.ServerError;
import model.AuthData;
import model.ErrorResponse;
import model.GameArray;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class ServerCalls {
    private static String url;

    ServerCalls(String url) {
        ServerCalls.url = url;
    }

    public Object getGames() {
        try {
            var method = "GET";
            var body = "";
            HttpURLConnection http = sendRequest(ServerCalls.url + "/game", method, body);
            Object result = receiveResponse(http, GameArray.class);
            return  result;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public AuthData loginRequest(UserData userData) throws ServerError {
        try {
            var method = "POST";
            HttpURLConnection http = sendRequest(ServerCalls.url + "/session", method, new Gson().toJson(userData));
            Object result = receiveResponse(http, String.class);
            // If object isn't an authtoken, it won't get here
            return (AuthData) result;
        } catch (ServerError | URISyntaxException | IOException e) {
            throw new ServerError(500, e.getMessage());
        }
    }

    public AuthData registerRequest(UserData userData) throws ServerError {
        try {
            var method = "POST";
            HttpURLConnection http = sendRequest(ServerCalls.url + "/user", method, new Gson().toJson(userData));
            Object result = receiveResponse(http, AuthData.class);
            // If object isn't an authtoken, it won't get here
            return (AuthData) result;
        } catch (ServerError | URISyntaxException | IOException e) {
            throw new ServerError(500, e.getMessage());
        }
    }


        private static HttpURLConnection sendRequest(String url, String method, String body) throws URISyntaxException, IOException, IOException {
            URI uri = new URI(url);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod(method);
            writeRequestBody(body, http);
            http.connect();
            System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
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
            //System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody);
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
