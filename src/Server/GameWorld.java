package Server;

import Game.GameModes.GameMode;
import Game.Models.Field;
import General.MultiBomb;
import Server.Items.ServerArrow;
import Server.Items.ServerBomb;
import Server.Items.ServerSword;
import Server.Messages.Socket.*;
import Server.Models.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static General.MultiBomb.LOGGER;

public class GameWorld extends Thread {
    /**
     * A threshold for random values to handle the rate at which items are spawned on the map
     */
    private static final float RANDOM_THRESHOLD = 0.2f;
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
    private final AtomicInteger currentItems = new AtomicInteger();
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

            synchronized (players) {
                gameMode.calculateWinner().ifPresent(s -> {
                    // if there is a winner, set the variable and stop the game loop
                    winner = s;
                    stopGame();
                });
            }

            // calculate sleep time for target tick rate
            long delta = System.currentTimeMillis() - startTime;

            if (delta < waitTime) {
                MultiBomb.sleep(waitTime - delta);
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
    public void removePlayer(String name) {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "removePlayer(" + name + ")"));
        synchronized (players) {
            // disconnected players are dead
            players.get(name).kill();
        }

        // send update about dead player to all players
        lobby.sendToAllPlayers(players.get(name).playerState);
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "removePlayer(" + name + ")"));
    }

    /**
     * Stop the game loop
     */
    public void stopGame() {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "stopGame()"));
        isRunning = false;
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "stopGame()"));
    }

    /**
     * Handle hit on a field, the player that is using the item can be hit
     *
     * @param from               the player that used the item
     * @param m                  coordinate on the map
     * @param n                  coordinate on the map
     * @param stoppedByBreakable item is stopped by breakable
     * @return indication if something was hit
     */
    private boolean handleHits(String from, int m, int n, boolean stoppedByBreakable) {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "handleHits(" + from + ")"));
        boolean hitSomething = false;

        // if pos is inside map
        if (m < Map.SIZE && m >= 0 && n < Map.SIZE && n >= 0) {
            // handle hits on the map
            hitSomething = handleMapHit(m, n, stoppedByBreakable);

            synchronized (players) {
                // a hit occurs if any of the players are on the hit's position
                players.values().forEach(p -> {
                    if (p.isAlive()
                            && (int) (p.position.y / Map.FIELD_SIZE) == m
                            && (int) (p.position.x / Map.FIELD_SIZE) == n) {
                        // hit player
                        gameMode.handleHit(p, players.get(from)).forEach(lobby::sendToAllPlayers);
                    }
                });
            }

        }
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "handleHits(" + from + ")"));
        return hitSomething;
    }

    /**
     * Handle hit from an item to a field
     *
     * @param m                  coordinate on the map
     * @param n                  coordinate on the map
     * @param stoppedByBreakable item is stopped by breakable
     * @return indication if something was hit
     */
    private boolean handleMapHit(int m, int n, boolean stoppedByBreakable) {
        boolean hitSomething = false;
        boolean hitBreakable = false;
        synchronized (map) {
            // get the field of the position
            Field field = Field.getItem(map.getField(m, n));

            if (!field.isPassable() && field != Field.SPAWN) {
                // field is solid or breakable, so it's a hit
                hitSomething = true;

                if (field == Field.BREAKABLE_0 || field == Field.BREAKABLE_1) {
                    LOGGER.info("Field broken at m=" + m + ", n=" + n);

                    hitBreakable = true;

                    // if stopped by breakable, register hit
                    hitSomething = stoppedByBreakable;

                    // set the field to ground
                    map.setField(m, n, Field.GROUND.id);
                }
            }
        }

        if (hitBreakable) {
            // field is destroyed, notify players
            lobby.sendToAllPlayers(new FieldDestroyed(m, n));

            // randomly spawn a new item at the fields position
            spawnItem(m, n, true);
        }
        return hitSomething;
    }

    /**
     * Handle hit of players in a certain distance to a middle point of a field
     * <p>
     * The player that is using the item does not get hit
     *
     * @param from   the player that used the item
     * @param x      pixel position of the circle center
     * @param y      pixel position of the circle center
     * @param radius the radius around the item
     */
    private void handlePlayerHitCircle(String from, float x, float y, float radius) {
        synchronized (players) {
            // a hit occurs if any of the players are on the hit's position
            players.values().forEach(p -> {
                float px = p.position.x;
                float py = p.position.y;

                double distance = Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2));

                if (p.isAlive()
                        && distance <= radius
                        && !p.name.equals(from)) {
                    // hit player
                    gameMode.handleHit(p, players.get(from)).forEach(lobby::sendToAllPlayers);
                }
            });
        }
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
            // get the PlayerConnection for the player
            playerConnection = lobby.players.get(player.name);

            // set the last position of the player
            player.position = playerConnection.lastPosition;

            if (player.position != null) {
                // notify all players about the new position
                lobby.sendToAllPlayers(player.position);

                // position on the map
                int m = (int) (player.position.y / Map.FIELD_SIZE);
                int n = (int) (player.position.x / Map.FIELD_SIZE);

                Field field;
                synchronized (map) {
                    // Collect items
                    field = Field.getItem(map.getField(m, n));
                    if (field.id < 0) {
                        // item is collectible
                        map.setField(m, n, Field.GROUND.id);
                        currentItems.decrementAndGet();

                        LOGGER.info(player.name + " collected " + field.name);
                    }
                }
                if (field.id < 0) {
                    synchronized (players) {
                        // handle the collected item
                        player.playerState.collectItem(field, true);
                    }

                    // notify all players about the collected item and the new player state
                    lobby.sendToAllPlayers(new ItemCollected(player.name, field, m, n));
                    lobby.sendToAllPlayers(player.playerState);
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
                lobby.sendToAllPlayers(iA);

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
                                iA.direction
                        );
                        break;
                    case ServerSword.NAME:
                        ServerSword.serverLogic(
                                (m, n) -> {
                                    float x = (float) n * Map.FIELD_SIZE + Map.FIELD_SIZE * 0.5f;
                                    float y = (float) m * Map.FIELD_SIZE + Map.FIELD_SIZE * 0.5f;

                                    handleMapHit(m, n, false);
                                    handleMapHit(m + 1, n, false);
                                    handleMapHit(m, n + 1, false);
                                    handleMapHit(m - 1, n, false);
                                    handleMapHit(m, n - 1, false);

                                    handlePlayerHitCircle(player.name, x, y, Map.FIELD_SIZE * 1.5f);
                                    return false;
                                },
                                item_m,
                                item_n
                        );
                        break;
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
        float randomOffset = 0;
        synchronized (players) {
            alivePlayers = players.values().stream().filter(Player::isAlive).count();
            if (alivePlayers > 2) {
                randomOffset = (alivePlayers - 2) * 0.05f;
            }
        }

        synchronized (map) {
            if (currentItems.get() < alivePlayers + 2) {
                float random_threshold;
                if (fromBreakable) {
                    random_threshold = RANDOM_THRESHOLD * 2;
                } else {
                    random_threshold = (RANDOM_THRESHOLD + randomOffset) / Server.ticksPerSecond;
                }

                if (random.nextFloat() < random_threshold && map.getField(m, n) == Field.GROUND.id) {

                    // get random new item
                    int index = random.nextInt(gameMode.items.length);

                    LOGGER.info(String.format("Set new item %s at m=%d, n=%d",
                            Field.getItem(gameMode.items[index]).name, m, n));

                    // set new item on map
                    map.setField(m, n, gameMode.items[index]);

                    currentItems.incrementAndGet();

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
    private void spawnItem() {
        LOGGER.config(String.format("Entering: %s %s", GameWorld.class.getName(), "spawnItem()"));
        // set number for maximum number of tries a new random position is generated
        int maxTries = 40;
        for (int i = 0; i < maxTries; i++) {
            // generate random position that is not on the border of the map
            int m = random.nextInt(Map.SIZE - 2) + 1;
            int n = random.nextInt(Map.SIZE - 2) + 1;

            synchronized (players) {
                if (players.values().stream().anyMatch(p -> {
                    int pm = (int) (p.position.y / Map.FIELD_SIZE);
                    int pn = (int) (p.position.x / Map.FIELD_SIZE);

                    return Math.abs(m - pm) + Math.abs(n - pn) < 3;
                })) {
                    continue;
                }
            }

            // check if location is ground on the map
            if (map.getField(m, n) == Field.GROUND.id) {
                // spawn new item at position
                spawnItem(m, n, false);
                // break loop because field with ground was found
                break;
            }
        }
        LOGGER.config(String.format("Exiting: %s %s", GameWorld.class.getName(), "spawnItem()"));
    }
}
