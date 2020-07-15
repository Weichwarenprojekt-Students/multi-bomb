package Server.Messages.Socket;

import Game.Models.Direction;
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
     * The direction of usage
     */
    public Direction direction;
    /**
     * The position at which the item is used
     */
    public int m, n;

    /**
     * Constructor
     */
    public ItemAction(String itemId, String playerId, Direction direction, int m, int n) {
        // Initialize message with type
        super(Message.ITEM_ACTION_TYPE);
        this.itemId = itemId;
        this.playerId = playerId;
        this.direction = direction;
        this.m = m;
        this.n = n;
    }
}
