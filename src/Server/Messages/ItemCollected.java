package Server.Messages;

import Game.Models.Field;

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
