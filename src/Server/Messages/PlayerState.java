package Server.Messages;

public class PlayerState extends Message {
    /**
     * The player name
     */
    public String playerId;
    /**
     * The player's health
     */
    public int health;
    /**
     * The player's number of kills
     */
    public int kills;

    /**
     * Constructor
     */
    public PlayerState() {
        // Initialize message with type
        super(Message.PLAYER_STATE_TYPE);
    }
}
