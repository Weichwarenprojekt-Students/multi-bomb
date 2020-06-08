package Server.Messages.Socket;

import Game.Models.Field;
import Server.Messages.Message;

public class ItemCollected extends Message {
    /**
     * The collected item
     */
    public Field item;
    /**
     * The player who collected the item
     */
    public String playerId;

    /**
     * Constructor
     */
    public ItemCollected() {
        // Initialize message with type
        super(Message.ITEM_COLLECTED_TYPE);
    }
}
