package Server.Items;

/**
 * The base class for a usable item
 */
public abstract class ServerItem {
    public interface ItemCallback {
        boolean callback(int m, int n);
    }
}
