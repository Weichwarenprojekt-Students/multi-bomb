package Server.Messages.Socket;

import Server.Messages.Message;

public class FieldDestroyed extends Message {
    /**
     * The row on the map
     */
    public int m;
    /**
     * The column on the map
     */
    public int n;

    /**
     * Constructor
     *
     * @param m coordinate on the map
     * @param n coordinate on the map
     */
    public FieldDestroyed(int m, int n) {
        // Initialize message with type
        super(Message.FIELD_DESTROYED_TYPE);

        this.m = m;
        this.n = n;
    }
}
