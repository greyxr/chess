package server;

import ui.Client;
import webSocketMessages.serverMessages.MessageAdapter;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.CommandAdapter;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WSClient extends Endpoint {

    public static void main(String[] args) throws Exception {
//        var ws = new WSClient();
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Enter a message you want to echo");
//        while (true) {
//            ws.send(scanner.nextLine());
//        }
    }

    public Session session;

    Client client;
    int port;

    public WSClient(Client client, int port) {
        this.client = client;
        this.port = port;
    }

    public void setup() {
        try {
            URI uri = new URI("ws://localhost:" + this.port + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
            System.out.println("Websocket set up.");
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    MessageAdapter messageAdapter = new MessageAdapter();
                    ServerMessage serverMessage = messageAdapter.fromJson(message);
                    client.notify(serverMessage);
                }
            });
        } catch(Exception ex) {
            System.out.println(("Exception caught: " + ex.getMessage()));
        }
    }

    public void send(String msg) throws Exception {
        if (this.session == null) {
            setup();
        }
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Websocket opened.");
    }
}