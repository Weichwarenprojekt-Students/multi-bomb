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
            LOBBY_STATE_TYPE = "lobbyState", PLAYER_STATE_TYPE = "playerState";

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
        return gson.fromJson(json, Message.class);
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
