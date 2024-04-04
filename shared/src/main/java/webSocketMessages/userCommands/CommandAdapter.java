package webSocketMessages.userCommands;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CommandAdapter  {
    GsonBuilder builder;
    Gson gson;

    CommandAdapter() {
        builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class, new adapter());
        gson = builder.create();
    }

    public UserGameCommand fromJson(String json) {
        return gson.fromJson(json, UserGameCommand.class);
    }
    private static class adapter implements JsonDeserializer<UserGameCommand> {
        @Override
        public UserGameCommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String typeString = jsonObject.get("commandType").getAsString();
            UserGameCommand.CommandType command = UserGameCommand.CommandType.valueOf(typeString);

            return switch(command) {
                case JOIN_PLAYER -> context.deserialize(jsonElement, JoinPlayer.class);
                case JOIN_OBSERVER -> context.deserialize(jsonElement, JoinObserver.class);
                case MAKE_MOVE -> context.deserialize(jsonElement, MakeMove.class);
                case LEAVE -> context.deserialize(jsonElement, Leave.class);
                case RESIGN -> context.deserialize(jsonElement, Resign.class);
            };
        }
    }
}