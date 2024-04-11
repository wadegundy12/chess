package webSocketMessages.serverMessages;

import com.google.gson.*;
import webSocketMessages.userCommands.*;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage))
            return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    @Override
    public String toString() {return new Gson().toJson(this);}

    public static class ServerMessageDeserializer implements JsonDeserializer<UserGameCommand> {
        @Override
        public UserGameCommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String typeString = jsonObject.get("serverMessageType").getAsString();
            ServerMessageType serverMessageType = ServerMessageType.valueOf(typeString);

            return switch(serverMessageType){
                case LOAD_GAME -> context.deserialize(jsonElement,LoadGame.class);
                case ERROR -> context.deserialize(jsonElement,Error.class);
                case NOTIFICATION -> context.deserialize(jsonElement,Notification.class);
            };
        }
    }


}
