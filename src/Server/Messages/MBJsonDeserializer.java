package Server.Messages;

import Game.Models.Map;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MBJsonDeserializer implements JsonDeserializer<Message> {

    @Override
    public Message deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {

        // turn json message into generic JsonObject
        JsonObject jsonObject = json.getAsJsonObject();

        // parse 'type' field value into String
        JsonElement jsonType = jsonObject.get("type");
        String type = jsonType.getAsString();

        Message typeModel;

        // deserialize message depending on the type value
        switch (type) {
            case Message.POSITION_TYPE:
                typeModel = context.deserialize(json, Position.class);
                break;
            case Message.ITEM_ACTION_TYPE:
                typeModel = context.deserialize(json, ItemAction.class);
                break;
            case Message.MAP_TYPE:
                typeModel = context.deserialize(json, Map.class);
                break;
            case Message.FIELD_DESTROYED_TYPE:
                typeModel = context.deserialize(json, FieldDestroyed.class);
                break;
            case Message.GAME_STATE_TYPE:
                typeModel = context.deserialize(json, GameState.class);
                break;
            case Message.ITEM_COLLECTED_TYPE:
                typeModel = context.deserialize(json, ItemCollected.class);
                break;
            case Message.LOBBY_STATE_TYPE:
                typeModel = context.deserialize(json, LobbyState.class);
                break;
            case Message.PLAYER_STATE_TYPE:
                typeModel = context.deserialize(json, PlayerState.class);
                break;
            case Message.SERVER_INFO_TYPE:
                typeModel = context.deserialize(json, ServerInfo.class);
                break;
            case Message.LOBBY_INFO_TYPE:
                typeModel = context.deserialize(json, LobbyInfo.class);
                break;
            default:
                typeModel = new Message(type) {};
        }

        return typeModel;
    }

}
