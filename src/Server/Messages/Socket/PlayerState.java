package Server.Messages.Socket;

import Server.Messages.Message;
import Server.Models.Player;

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
     *
     * @param player the player the state is from
     */
    public PlayerState(Player player) {
        // Initialize message with type
        super(Message.PLAYER_STATE_TYPE);

        health = player.health;
        kills = player.kills;
    }
}
