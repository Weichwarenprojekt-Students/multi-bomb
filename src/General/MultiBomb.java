package General;

import Server.Server;

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

    /**
     * Start a timed action
     *
     * @param waitTime  for each iteration
     * @param action    to be executed
     */
    public static void startTimedAction(long waitTime, TimedAction action) {
        new Thread(() -> {
            // The time variables
            long startTime = System.currentTimeMillis(), deltaTime, lastTime = startTime - waitTime;

            // The running variable
            boolean running = true;

            while (running) {
                // Update the global times
                deltaTime = System.currentTimeMillis() - lastTime;
                lastTime = System.currentTimeMillis();

                // Record the start time
                long start = System.currentTimeMillis();

                // Execute the action
                running = action.action(deltaTime, System.currentTimeMillis() - startTime);

                // Wait for the next run
                targetRefreshRate(start, waitTime);
            }
        }).start();
    }

    /**
     * Target the refresh rate by waiting for the next run
     *
     * @param start time in milliseconds
     */
    private static void targetRefreshRate(long start, long waitTime) {
        long localDelta = System.currentTimeMillis() - start;
        if (localDelta < waitTime) {
            sleep(waitTime - localDelta);
        }
    }

    /**
     * Let a thread sleep for a certain time
     *
     * @param time to be in sleep mode
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * The interface for timed actions
     */
    public interface TimedAction {
        boolean action(long deltaTime, long totalTime);
    }
}
