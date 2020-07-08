package General;

import Server.Server;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiBomb {
    /**
     * Logger
     */
    public static final Logger LOGGER = Logger.getLogger(MultiBomb.class.getName());

    /**
     * Start Multi-Bomb and parse command line args
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            LOGGER.addHandler(consoleHandler);
            consoleHandler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);

            if (!"-s".equals(args[0]) || args.length < 4) {
                System.out.println("To start only the server use the following syntax:");
                System.out.println("-s <serverName> <tickRate> <maxLobbies>");
            } else {
                String serverName = args[1];
                try {
                    int tickRate = Integer.parseInt(args[2]);
                    int maxLobbies = Integer.parseInt(args[3]);

                    if (serverName.equals("")) {
                        System.out.println("Server name cannot be empty");
                    } else if (tickRate == 64 || tickRate == 128) {
                        new Server(serverName, tickRate, maxLobbies).run();
                    } else {
                        System.out.println("TickRate must be 64 or 128");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("TickRate and maxLobbies must be integers!");
                }
            }
        } else {
            MB.startGame();
        }
    }
}
