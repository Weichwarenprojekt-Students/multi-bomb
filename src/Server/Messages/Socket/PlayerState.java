package Server.Messages.Socket;

import Game.Lobby;
import Game.Models.Field;
import Game.Models.Upgrades;
import General.Sound.SoundControl;
import General.Sound.SoundEffect;
import Server.Messages.Message;

public class PlayerState extends Message {
    /**
     * Default health of a player
     */
    public static final int DEFAULT_HEALTH = 1;
    /**
     * Maximum health of a player
     */
    public static final int MAX_HEALTH = 3;
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
    public Upgrades upgrades = new Upgrades();
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

        health = DEFAULT_HEALTH;
    }

    /**
     * Constructor
     *
     * @param name name of the player
     */
    public PlayerState(String name) {
        this();
        playerId = name;
    }

    /**
     * Update health and kills
     *
     * @param state new state
     */
    public void update(PlayerState state) {
        this.kills = state.kills;

        // Check if the player should die
        if (state.health <= 0) {
            Lobby.players.get(state.playerId).die(false);
        } else if (state.health < this.health) {
            this.health = state.health;
            Lobby.players.get(state.playerId).takeHit();
        } else {
            this.health = state.health;
        }
    }

    /**
     * Check if player is alive
     *
     * @return true if player is alive
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Handle collected item
     *
     * @param item           the collected item
     * @param increaseHealth true if the health shall be increased
     */
    public void collectItem(Field item, boolean increaseHealth) {
        switch (item) {
            case BOMB:
                // increment number of bombs the player can place
                if (upgrades.bombCount < Upgrades.MAX_BOMB_COUNT) {
                    upgrades.bombCount++;
                }
                break;
            case SPEED:
                // increment speed of player
                if (upgrades.speed < Upgrades.MAX_SPEED) {
                    upgrades.speed++;
                }
                break;
            case HEART:
                // increment health
                if (health < MAX_HEALTH && increaseHealth) {
                    health++;
                }
                break;
            case EXPLOSION:
                // increment the size of the bomb explosion
                if (upgrades.bombSize < Upgrades.MAX_BOMB_SIZE) {
                    upgrades.bombSize++;
                }
                break;
        }
    }
}
