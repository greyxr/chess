package server;

import model.GameData;

import java.io.IOException;
import java.io.InputStream;
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
        ServerCalls serverCalls = new ServerCalls("http://localhost:8080");
        serverCalls.getGames();
        return null;
//        try {
//            var url = new URL("http://localhost:8080/game");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setDoOutput(true);
//            conn.connect();
//            try (InputStream respBody = conn.getInputStream()) {
//                byte[] bytes = new byte[respBody.available()];
//                respBody.read(bytes);
//                System.out.println(new String(bytes));
//            }
//        } catch (Exception ex) {
//            System.out.printf("ERROR: %s\n", ex);
//        }
//        return null;
    }
}
