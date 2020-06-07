import General.MB;
import Server.Server;

public class MultiBomb {
    /**
     * Start Multi-Bomb and parse command line args
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            if ("-s".equals(args[0])) {
                new Server("Multi-Bomb server").run();
            } else {
                System.out.println("Option not recognized, try -s to start server only");
            }
        } else {
            MB.startGame();
        }
    }
}
