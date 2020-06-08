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
     * Players ranked from best to worst
     */
    public String[] playerRanking;

    /**
     * Constructor
     */
    public GameState() {
        // Initialize message with type
        super(Message.GAME_STATE_TYPE);
    }
}
