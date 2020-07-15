package Server.Messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Message {
    /**
     * Message types
     */
    public static final String POSITION_TYPE = "position", ITEM_ACTION_TYPE = "itemAction",
            MAP_TYPE = "map", FIELD_DESTROYED_TYPE = "fieldDestroyed",
            GAME_STATE_TYPE = "gameState", ITEM_COLLECTED_TYPE = "itemCollected",
            LOBBY_STATE_TYPE = "lobbyState", PLAYER_STATE_TYPE = "playerState",
            SERVER_INFO_TYPE = "serverInfo", LOBBY_INFO_TYPE = "lobbyInfo",
            JOIN_LOBBY_TYPE = "joinLobby", CREATE_LOBBY_TYPE = "createLobby",
            INVALID_TYPE = "invalidMessage", ERROR_MESSAGE_TYPE = "errorMessage",
            CLOSE_CONNECTION_TYPE = "closeConnection", NEW_ITEM_TYPE = "newItem",
            RESPAWN_TYPE = "respawn";

    /**
     * Gson object with deserializer for Message.class
     */
    public static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MBJsonDeserializer()).create();

    /**
     * Type of the message
     */
    public final String type;

    /**
     * Constructor
     *
     * @param type of the message
     */
    public Message(String type) {
        this.type = type;
    }

    /**
     * Generate Message object from json string
     *
     * @param json string of the message
     * @return a new Message object
     */
    public static Message fromJson(String json) {
        try {
            return gson.fromJson(json, Message.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(INVALID_TYPE) {};
        }
    }

    /**
     * Turn Message object into json string
     *
     * @return json String
     */
    public String toJson() {
        return gson.toJson(this);
    }
}
