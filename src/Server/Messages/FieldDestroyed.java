package Server.Messages;

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
     */
    public FieldDestroyed() {
        // Initialize message with type
        super(Message.FIELD_DESTROYED_TYPE);
    }
}
