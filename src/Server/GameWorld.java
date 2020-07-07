package Server;

import Game.GameModes.GameMode;
import Game.Items.Item;
import Game.Models.Field;
import Server.Items.Bomb;
import Server.Messages.Socket.*;
import Server.Models.Player;

import java.util.HashMap;
import java.util.Random;

public class GameWorld extends Thread {
    /**
     * The maximum number of items that can be spawned on the map
     */
    private static final int MAX_ITEMS = 17;
    /**
     * A threshold for random values to handle the rate at which items are spawned on the map
     */
    private static final float RANDOM_THRESHOLD = 0.1f / Server.ticksPerSecond;
    /**
     * Random object for random spawning of items
     */
    private final Random random;
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
     * The GameMode of the game
     */
    private final GameMode gameMode;
    /**
     * Indicate if the game is still running
     */
    private boolean isRunning = true;
    /**
     * The number of items currently on the map
     */
    private int currentItems;
    /**
     * The name of the winner of the game
     */
    private String winner = "";

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

        this.players = new HashMap<>();
        this.gameMode = GameMode.getMode(lobby.gameMode);

        this.random = new Random();

        // For every playerConnection in the lobby
        lobby.players.values().forEach(pc -> {
            // create new Player object
            Player player = new Player(pc.name);
            // save the player object
            players.put(pc.name, player);
            // put the player's PlayerState object in the gameMode
            gameMode.players.put(pc.name, player.playerState);
        });
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

            // handle events for every player
            players.values().forEach(this::handlePlayerEvents);

            // spawn an item randomly on the map
            spawnItem();

            synchronized (gameMode) {
                gameMode.calculateWinner().ifPresent(s -> {
                    // if there is a winner, set the variable and stop the game loop
                    winner = s;
                    stopGame();
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
        lobby.sendToAllPlayers(players.get(name).playerState);
    }

    /**
     * Stop the game loop
     */
    public synchronized void stopGame() {
        isRunning = false;
    }

    /**
     * Handle hits from an item in a row, interrupt if solid field is hit
     *
     * @param from      the player that used the item
     * @param positions the positions in a row, which the item hits
     * @return boolean indicating if a field or player was hit
     */
    public synchronized boolean handleHits(String from, int[][] positions) {
        // initialize the return value if something was hit to false
        boolean hitSomething = false;

        // for every position that was hit
        for (int[] pos : positions) {
            if (pos[0] >= Map.SIZE || pos[0] < 0 || pos[1] >= Map.SIZE || pos[1] < 0) {
                // if position is not on map, stop loop because all other positions will be outside of map as well
                break;
            }

            // get the field of the position
            Field field = Field.getItem(map.fields[pos[0]][pos[1]]);

            if (!field.isPassable() && field != Field.SPAWN) {
                // field is solid or breakable, so it's a hit
                hitSomething = true;

                if (field == Field.BREAKABLE_0 || field == Field.BREAKABLE_1) {
                    // field is destroyed, notify players
                    lobby.sendToAllPlayers(new FieldDestroyed(pos[0], pos[1]));

                    // set the field to ground
                    map.fields[pos[0]][pos[1]] = Field.GROUND.id;

                    // randomly spawn a new item at the fields position
                    spawnItem(pos);
                }
                // stop loop because row of hits is interrupted
                break;

            } else {
                // a hit occurs if any of the players are on the hit's position
                hitSomething = players.values().stream().anyMatch(p -> {
                    if (p.isAlive()
                            && (int) (p.position.y / Map.FIELD_SIZE) == pos[0]
                            && (int) (p.position.x / Map.FIELD_SIZE) == pos[1]) {
                        // hit player
                        gameMode.handleHit(p, players.get(from), map.spawns[lobby.players.get(p.name).color])
                                .forEach(lobby::sendToAllPlayers);

                        // hit a player
                        return true;
                    }
                    // hit no player
                    return false;
                });
            }
        }

        return hitSomething;
    }

    /**
     * Handle all player events for one player
     *
     * @param player the player
     */
    private synchronized void handlePlayerEvents(Player player) {
        if (player.isAlive()) {
            // get the PlayerConnection for the player
            PlayerConnection playerConnection = lobby.players.get(player.name);

            // set the last position of the player
            player.position = playerConnection.lastPosition;

            if (player.position != null) {
                // notify all players about the new position
                lobby.sendToAllPlayers(player.position);

                // position on the map
                int m = (int) (player.position.y / Map.FIELD_SIZE);
                int n = (int) (player.position.x / Map.FIELD_SIZE);

                // Collect items
                Field field = Field.getItem(map.fields[m][n]);
                if (field.id < 0) {
                    // item is collectible
                    map.fields[m][n] = Field.GROUND.id;
                    currentItems -= 1;
                    // handle the collected item
                    player.playerState.collectItem(field, true);

                    synchronized (lobby) {
                        // notify all players about the collected item and the new player state
                        lobby.sendToAllPlayers(new ItemCollected(player.name, field, m, n));
                        lobby.sendToAllPlayers(player.playerState);
                    }
                }

                // handle item actions of the player
                handleItemActions(player, playerConnection);
            }
        }

    }

    /**
     * Handle item actions of a player
     *
     * @param player           the player
     * @param playerConnection the corresponding PlayerConnection object
     */
    private synchronized void handleItemActions(Player player, PlayerConnection playerConnection) {
        // position on the map
        if (player.isAlive()) {
            int m = (int) (player.position.y / Map.FIELD_SIZE);
            int n = (int) (player.position.x / Map.FIELD_SIZE);

            synchronized (playerConnection.itemActions) {
                // for each item action
                playerConnection.itemActions.forEach(iA -> {
                    // send item action to all players
                    lobby.sendToAllPlayers(iA);
                    switch (iA.itemId) {
                        case Item.BOMB:
                            // start the server logic of the bomb
                            Bomb.serverLogic(
                                    (positions) -> handleHits(player.name, positions),
                                    m,
                                    n,
                                    player.playerState.upgrades.bombSize
                            );
                            break;
                    }
                });

                // clear the item actions from the player connection
                playerConnection.itemActions.clear();
            }
        }
    }

    /**
     * Randomly spawn a new item
     *
     * @param pos position at which the item should spawn
     */
    private synchronized void spawnItem(int[] pos) {
        if (currentItems < MAX_ITEMS
                && random.nextFloat() < RANDOM_THRESHOLD
                && map.fields[pos[0]][pos[1]] == Field.GROUND.id) {
            // get random new item
            int index = random.nextInt(gameMode.items.length);

            // set new item on map
            map.fields[pos[0]][pos[1]] = gameMode.items[index];

            // notify all players about new item
            lobby.sendToAllPlayers(new NewItem(Field.getItem(gameMode.items[index]), pos[0], pos[1]));
        }
    }

    /**
     * Randomly spawn a new item on a random location on the map
     */
    private synchronized void spawnItem() {
        // set number for maximum number of tries a new random position is generated
        int maxTries = 20;
        for (int i = 0; i < maxTries; i++) {
            // generate random position that is not on the border of the map
            int m = random.nextInt(Map.SIZE - 2) + 1;
            int n = random.nextInt(Map.SIZE - 2) + 1;

            // check if location is ground on the map
            if (map.fields[m][n] == Field.GROUND.id) {
                // spawn new item at position
                spawnItem(new int[]{m, n});
                // break loop because field with ground was found
                break;
            }
        }
    }
}
