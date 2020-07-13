package Server.Models;

import Server.Items.ServerProtection;
import Server.Messages.Socket.PlayerState;
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
     * State of the player
     */
    public PlayerState playerState;
    /**
     * Protection state of player
     */
    public boolean isProtected;

    /**
     * Constructor
     *
     * @param name name of the player
     */
    public Player(String name) {
        this.name = name;

        this.playerState = new PlayerState(name);
    }

    /**
     * Kill the player instantly
     */
    public synchronized void kill() {
        playerState.health = 0;
    }

    /**
     * Hit the player
     */
    public synchronized void hit() {
        if (!isProtected) {
            playerState.health -= 1;
            ServerProtection.serverLogic(ServerProtection.HIT_DURATION, this);
        }
    }

    /**
     * Protect the player from hit
     *
     * @param value set the protection to true or false
     */
    public synchronized void protect(boolean value) {
        isProtected = value;
    }

    /**
     * Indicate if the player's health is above 0
     *
     * @return boolean indicating if the player is still alive
     */
    public synchronized boolean isAlive() {
        return playerState.isAlive();
    }
}
