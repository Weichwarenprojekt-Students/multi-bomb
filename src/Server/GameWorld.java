package Server;

import Game.GameModes.GameMode;
import Game.Models.Field;
import Server.Items.ServerArrow;
import Server.Items.ServerBomb;
import Server.Messages.Socket.*;
import Server.Models.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static General.MultiBomb.LOGGER;

public class GameWorld extends Thread {
    /**
     * The maximum number of items that can be spawned on the map
     */
    private static final int MAX_ITEMS = 17;
    /**
     * A threshold for random values to handle the rate at which items are spawned on the map
     */
    private static final float RANDOM_THRESHOLD = 0.1f;
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
    private volatile int currentItems;
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
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "GameWorld()"));
        this.lobby = lobby;
        this.map = map;
        this.startTime = startTime;

        this.players = new HashMap<>();
        this.gameMode = GameMode.getMode(lobby.gameMode);

        this.random = new Random();

        LOGGER.info("Initialized new GameWorld");

        // For every playerConnection in the lobby
        lobby.players.values().forEach(pc -> {
            // create new Player object
            Player player = new Player(pc.name);
            // save the player object
            players.put(pc.name, player);
            // put the player's PlayerState object in the gameMode
            gameMode.players.put(pc.name, player.playerState);
        });
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "GameWorld()"));
    }

    @Override
    public void run() {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "run()"));
        try {
            // wait until the game loop can start
            Thread.sleep(startTime - System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info("Start game loop");

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
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "run()"));
    }

    /**
     * Remove player, used when socket connection is faulty
     *
     * @param name name of the player
     */
    public synchronized void removePlayer(String name) {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "removePlayer(" + name + ")"));
        // disconnected players are dead
        players.get(name).kill();

        // send update about dead player to all players
        lobby.sendToAllPlayers(players.get(name).playerState);
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "removePlayer(" + name + ")"));
    }

    /**
     * Stop the game loop
     */
    public synchronized void stopGame() {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "stopGame()"));
        isRunning = false;
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "stopGame()"));
    }

    /**
     * Handle hit on a field
     *
     * @param from           the player that used the item
     * @param m              coordinate on the map
     * @param n              coordinate on the map
     * @param throughPlayers item goes through players
     * @return indication if something was hit
     */
    public boolean handleHits(String from, int m, int n, boolean throughPlayers) {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "handleHits(" + from + ")"));
        boolean hitSomething = false;

        // if pos is inside map
        if (m < Map.SIZE && m >= 0 && n < Map.SIZE && n >= 0) {
            boolean hitBreakable = false;
            synchronized (map.fields) {
                // get the field of the position
                Field field = Field.getItem(map.fields[m][n]);

                if (!field.isPassable() && field != Field.SPAWN) {
                    // field is solid or breakable, so it's a hit
                    hitSomething = true;

                    if (field == Field.BREAKABLE_0 || field == Field.BREAKABLE_1) {
                        LOGGER.info("Field broken at m=" + m + ", n=" + n + " by " + from);

                        hitBreakable = true;

                        // set the field to ground
                        map.fields[m][n] = Field.GROUND.id;

                        // randomly spawn a new item at the fields position
                        spawnItem(m, n, true);
                    }
                }
            }

            if (hitBreakable) {
                synchronized (lobby) {
                    // field is destroyed, notify players
                    lobby.sendToAllPlayers(new FieldDestroyed(m, n));
                }
            }

            // a hit occurs if any of the players are on the hit's position
            hitSomething = players.values().stream().anyMatch(p -> {
                if (p.isAlive()
                        && (int) (p.position.y / Map.FIELD_SIZE) == m
                        && (int) (p.position.x / Map.FIELD_SIZE) == n) {
                    // hit player
                    gameMode.handleHit(p, players.get(from)).forEach(lobby::sendToAllPlayers);

                    // if item doesn't go through players, return true to register hit
                    return !throughPlayers;
                }
                // hit no player
                return false;
            }) | hitSomething;

        }
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "handleHits(" + from + ")"));
        return hitSomething;
    }

    /**
     * Handle all player events for one player
     *
     * @param player the player
     */
    private void handlePlayerEvents(Player player) {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "handlePlayerEvents(" + player.name + ")"));
        if (player.isAlive()) {

            PlayerConnection playerConnection;
            synchronized (lobby.players) {
                // get the PlayerConnection for the player
                playerConnection = lobby.players.get(player.name);
            }

            // set the last position of the player
            player.position = playerConnection.lastPosition;

            if (player.position != null) {
                synchronized (lobby) {
                    // notify all players about the new position
                    lobby.sendToAllPlayers(player.position);
                }

                // position on the map
                int m = (int) (player.position.y / Map.FIELD_SIZE);
                int n = (int) (player.position.x / Map.FIELD_SIZE);

                Field field;
                synchronized (map.fields) {
                    // Collect items
                    field = Field.getItem(map.fields[m][n]);
                    if (field.id < 0) {
                        // item is collectible
                        map.fields[m][n] = Field.GROUND.id;
                        currentItems--;
                        // handle the collected item
                        player.playerState.collectItem(field, true);

                        LOGGER.info(player.name + " collected " + field.name);
                    }
                }
                synchronized (lobby) {
                    if (field.id < 0) {
                        // notify all players about the collected item and the new player state
                        lobby.sendToAllPlayers(new ItemCollected(player.name, field, m, n));
                        lobby.sendToAllPlayers(player.playerState);
                    }
                }

                // handle item actions of the player
                handleItemActions(player, playerConnection);
            }
        }
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "handlePlayerEvents(" + player.name + ")"));
    }

    /**
     * Handle item actions of a player
     *
     * @param player           the player
     * @param playerConnection the corresponding PlayerConnection object
     */
    private void handleItemActions(Player player, PlayerConnection playerConnection) {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "handleItemActions(" + player.name + ")"));
        // position on the map
        if (player.isAlive()) {
            int item_m = (int) (player.position.y / Map.FIELD_SIZE);
            int item_n = (int) (player.position.x / Map.FIELD_SIZE);

            List<ItemAction> itemActions;

            synchronized (playerConnection.itemActions) {
                // for each item action
                itemActions = new ArrayList<>(playerConnection.itemActions);
                playerConnection.itemActions.clear();
            }

            itemActions.forEach(iA -> {
                // send item action to all players
                synchronized (lobby) {
                    lobby.sendToAllPlayers(iA);
                }

                LOGGER.info(player.name + " used item " + iA.itemId);

                switch (iA.itemId) {
                    case ServerBomb.NAME:
                        // start the server logic of the bomb
                        ServerBomb.serverLogic(
                                (hit_m, hit_n) -> handleHits(player.name, hit_m, hit_n, true),
                                item_m,
                                item_n,
                                player.playerState.upgrades.bombSize
                        );
                        break;
                    case ServerArrow.NAME:
                        ServerArrow.serverLogic(
                                (hit_m, hit_n) -> handleHits(player.name, hit_m, hit_n, false),
                                item_m,
                                item_n,
                                player.position.direction
                        );

                }
            });
        }

        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "handleItemActions(" + player.name + ")"));
    }

    /**
     * Randomly spawn a new item
     *
     * @param m coordinate on the map
     * @param n coordinate on the map
     */
    private void spawnItem(int m, int n, boolean fromBreakable) {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "spawnItem(m, n)"));

        long alivePlayers;
        synchronized (players) {
            alivePlayers = players.values().stream().filter(Player::isAlive).count();
        }

        synchronized (map.fields) {
            if (currentItems < MAX_ITEMS) {
                float random_threshold;
                if (currentItems < alivePlayers || fromBreakable) {
                    random_threshold = RANDOM_THRESHOLD;
                } else {
                    random_threshold = RANDOM_THRESHOLD / Server.ticksPerSecond / 10;
                }

                if (random.nextFloat() < random_threshold
                        && map.fields[m][n] == Field.GROUND.id) {

                    // get random new item
                    int index = random.nextInt(gameMode.items.length);

                    LOGGER.info(String.format("Set new item %s at m=%d, n=%d",
                            Field.getItem(gameMode.items[index]).name, m, n));

                    // set new item on map
                    map.fields[m][n] = gameMode.items[index];

                    currentItems++;

                    // notify all players about new item
                    lobby.sendToAllPlayers(new NewItem(Field.getItem(gameMode.items[index]), m, n));
                }
            }
        }
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "spawnItem(m, n)"));
    }

    /**
     * Randomly spawn a new item on a random location on the map
     */
    private synchronized void spawnItem() {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "spawnItem()"));
        // set number for maximum number of tries a new random position is generated
        int maxTries = 20;
        for (int i = 0; i < maxTries; i++) {
            // generate random position that is not on the border of the map
            int m = random.nextInt(Map.SIZE - 2) + 1;
            int n = random.nextInt(Map.SIZE - 2) + 1;

            // check if location is ground on the map
            if (map.fields[m][n] == Field.GROUND.id) {
                // spawn new item at position
                spawnItem(m, n, false);
                // break loop because field with ground was found
                break;
            }
        }
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "spawnItem()"));
    }
}
