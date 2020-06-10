package Server.Messages.Socket;

import Server.Messages.Message;

public class ItemAction extends Message {
    /**
     * The item which is used
     */
    public String itemId;
    /**
     * The player who uses the item
     */
    public String playerId;

    /**
     * Constructor
     */
    public ItemAction() {
        // Initialize message with type
        super(Message.ITEM_ACTION_TYPE);
    }
}
