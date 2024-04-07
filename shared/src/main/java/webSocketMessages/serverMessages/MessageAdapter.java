package webSocketMessages.serverMessages;

import com.google.gson.*;
import java.lang.reflect.Type;

public class MessageAdapter {
    GsonBuilder builder;
    Gson gson;

    public MessageAdapter() {
        builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new Adapter());
        gson = builder.create();
    }

    public ServerMessage fromJson(String json) {
        return gson.fromJson(json, ServerMessage.class);
    }
    private static class Adapter implements JsonDeserializer<ServerMessage> {
        @Override
        public ServerMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String typeString = jsonObject.get("serverMessageType").getAsString();
            ServerMessage.ServerMessageType messageType = ServerMessage.ServerMessageType.valueOf(typeString);

            return switch(messageType) {
                case NOTIFICATION -> context.deserialize(jsonElement, Notification.class);
                case ERROR -> context.deserialize(jsonElement, Error.class);
                case LOAD_GAME -> context.deserialize(jsonElement, LoadGame.class);
            };
        }
    }
}