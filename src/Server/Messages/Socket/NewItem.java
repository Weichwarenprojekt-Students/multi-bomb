package Server.Messages.Socket;

import Game.Models.Field;
import Server.Messages.Message;

public class NewItem extends Message {
    /**
     * The new item
     */
    public Field item;
    /**
     * The position at which the item is spawned
     */
    public int m, n;

    /**
     * Constructor
     *
     * @param item the new item
     * @param m    coordinate on the map where the item is spawned
     * @param n    coordinate on the map where the item is spawned
     */
    public NewItem(Field item, int m, int n) {
        // Initialize message with type
        super(Message.NEW_ITEM_TYPE);

        this.item = item;
        this.m = m;
        this.n = n;
    }
}
