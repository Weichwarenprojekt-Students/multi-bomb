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
     * The position at which the item is collected
     */
    public int m, n;

    /**
     * Constructor
     *
     * @param playerId the player that collected the item
     * @param item     the item that is collected
     * @param m        coordinate on the map where item is collected
     * @param n        coordinate on the map where item is collected
     */
    public ItemCollected(String playerId, Field item, int m, int n) {
        // Initialize message with type
        super(Message.ITEM_COLLECTED_TYPE);

        this.playerId = playerId;
        this.item = item;
        this.m = m;
        this.n = n;
    }
}
