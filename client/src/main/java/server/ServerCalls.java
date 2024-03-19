package server;

import com.google.gson.Gson;
import model.ErrorResponse;
import model.GameArray;

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

    public GameArray getGames() {
        try {
            var method = "GET";
            var body = "";
            HttpURLConnection http = sendRequest(ServerCalls.url + "/game", method, body);
            receiveResponse(http, new GameArray(null));
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
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

        private static Object receiveResponse(HttpURLConnection http, Object type) throws IOException {
            var statusCode = http.getResponseCode();
            var statusMessage = http.getResponseMessage();
            if (statusCode == 200 || statusCode == 201) {
                return readResponseBody(http, type);
            } else {
                return new ErrorResponse(statusMessage);
            }
            //System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody);
        }

        private static Object readResponseBody(HttpURLConnection http, Object type) throws IOException {
            Object responseBody = "";
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                responseBody = new Gson().fromJson(inputStreamReader, type.getClass());
            }
            return responseBody;
        }

}
