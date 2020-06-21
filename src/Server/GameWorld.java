package Server;

import Server.Messages.Socket.Map;
import Server.Messages.Socket.PlayerState;
import Server.Models.Player;

import java.util.HashMap;

public class GameWorld extends Thread {
    /**
     * Lobby the GameWorld is running in
     */
    private final Lobby lobby;
    /**
     * Map of all player names to the according player objects
     */
    private final HashMap<String, Player> players;
    /**
     * The map of the game
     */
    private final Map map;
    /**
     * Time the game loop has to sleep for in order to reach the desired tick rate
     */
    private final long waitTime = 1000 / Server.ticksPerSecond;
    /**
     * The time at which the game loop can start
     */
    private final long startTime;
    /**
     * Indicate if the game is still running
     */
    public boolean isRunning = true;

    /**
     * Constructor
     *
     * @param lobby     the lobby the GameWorld runs in
     * @param map       the map of the game
     * @param startTime the time at which the game loop should start
     */
    public GameWorld(Lobby lobby, Map map, long startTime) {
        this.lobby = lobby;
        this.map = map;
        this.startTime = startTime;

        players = new HashMap<>();

        lobby.players.values().forEach(pc -> players.put(pc.name, new Player(pc.name)));
    }

    @Override
    public void run() {
        try {
            // wait until the game loop can start
            Thread.sleep(startTime - System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            long startTime = System.currentTimeMillis();

            synchronized (players) {
                players.values().forEach(p -> {
                    if (p.isAlive()) {
                        // update the position of each player who is alive
                        p.position = lobby.players.get(p.name).lastPosition;
                    }
                });

                players.values().forEach(p -> {
                    if (p.isAlive()) {
                        // send the position of every alive player to all connected players
                        lobby.sendToAllPlayers(p.position);
                    }
                });
            }

            // calculate sleep time for target tick rate
            long delta = System.currentTimeMillis() - startTime;

            if (delta < waitTime) {
                try {
                    Thread.sleep(waitTime - delta);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // pick any player as winner, as long no further game logic is implemented
        String winner = players.values().iterator().next().name;

        // end the game
        lobby.endGame(winner);
    }

    /**
     * Remove player, used when socket connection is faulty
     *
     * @param name name of the player
     */
    public synchronized void removePlayer(String name) {
        // disconnected players are dead
        players.get(name).kill();

        // send update about dead player to all players
        lobby.sendToAllPlayers(new PlayerState(players.get(name)));
    }
}
