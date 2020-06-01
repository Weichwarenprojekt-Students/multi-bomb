package Server;

public class Server implements Runnable {
    /**
     * Port for UPD Broadcast server discovery
     */
    public static int UDP_PORT = 42420;

    /**
     * Run server
     */
    @Override
    public void run() {
        // start new UDP DiscoveryThread
        new Thread(new DiscoveryThread()).start();
    }
}
