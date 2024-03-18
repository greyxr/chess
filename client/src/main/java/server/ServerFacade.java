package server;

import model.GameData;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ServerFacade {
    static String url;
    public ServerFacade(int port) {
        String url = "localhost:" + port;
        ServerFacade.url = url;
    }

    public Collection<GameData> getGames() {
        try {
//            URL url = new URL(ServerFacade.url + "/games");
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.connect();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + ServerFacade.url + "/game")).GET().build();
            CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            // Handle response asynchronously
            responseFuture.thenAccept(response -> {
                        // Print response status code
                        System.out.println("Response Code: " + response.statusCode());
                        System.out.println(response.statusCode());
                System.out.println(response.body());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
