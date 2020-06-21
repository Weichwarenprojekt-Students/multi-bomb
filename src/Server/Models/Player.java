package Server.Models;

import Server.Messages.Socket.Position;

public class Player {
    /**
     * Name of the player
     */
    public final String name;
    /**
     * Postition of the player
     */
    public Position position;
    /**
     * Health of the player
     */
    public int health;
    /**
     * Number of kills the player has done
     */
    public int kills;

    /**
     * Constructor
     *
     * @param name name of the player
     */
    public Player(String name) {
        this.name = name;

        health = 3;
        kills = 0;
    }

    /**
     * Kill the player instantly
     */
    public void kill() {
        health = 0;
    }

    /**
     * Indicate if the player's health is above 0
     *
     * @return boolean indicating if the player is still alive
     */
    public boolean isAlive() {
        return health != 0;
    }
}
