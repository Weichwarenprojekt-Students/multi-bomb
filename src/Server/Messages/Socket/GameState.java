package Server.Messages.Socket;

import Server.Messages.Message;

public class GameState extends Message {
    /**
     * Possible states of the game
     */
    public static final int PREPARING = 0, RUNNING = 1, FINISHED = 2;

    /**
     * Specific state of the game
     */
    public int state;
    /**
     * Timestamp of the message
     */
    public long timestamp;
    /**
     * Winner of the game
     */
    public String winner;

    /**
     * Constructor
     *
     * @param state  state of the game
     * @param winner winner of the game
     */
    public GameState(int state, String winner, long timestamp) {
        // Initialize message with type
        super(Message.GAME_STATE_TYPE);

        this.state = state;
        this.timestamp = timestamp;
        this.winner = winner;
    }

    /**
     * Get GameState with PREPARING state
     *
     * @return new GameState objectd
     */
    public static GameState preparing() {
        return new GameState(PREPARING, null, 0);
    }

    /**
     * Get GameState with RUNNING state
     *
     * @param timestamp time of countdown start
     * @return new GameState object
     */
    public static GameState running(long timestamp) {
        return new GameState(RUNNING, null, timestamp);
    }

    /**
     * Get GameState with FINISHED state
     *
     * @param winner winner of the game
     * @return new GameState object
     */
    public static GameState finished(String winner) {
        return new GameState(FINISHED, winner, 0);
    }
}
