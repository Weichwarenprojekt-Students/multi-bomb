package Server.Messages.Socket;

import Game.Models.Upgrades;
import Server.Messages.Message;

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
     * The player's upgrades
     */
    public Upgrades upgrades;
    /**
     * The player's item
     */
    public String item;

    /**
     * Constructor
     */
    public PlayerState() {
        // Initialize message with type
        super(Message.PLAYER_STATE_TYPE);
    }
}
