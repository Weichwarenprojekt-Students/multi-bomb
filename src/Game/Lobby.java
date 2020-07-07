package Game;

import Editor.MapManager;
import Game.GameModes.BattleRoyale;
import Game.GameModes.GameMode;
import Game.Models.Player;
import General.MB;
import Menu.DetailedLobbyView;
import Menu.ServerView;
import Server.Messages.Message;
import Server.Messages.Socket.GameState;
import Server.Messages.Socket.LobbyState;
import Server.Messages.Socket.Map;
import Server.Messages.Socket.Position;
import Server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Lobby {
    /**
     * The players
     */
    public static final HashMap<String, Player> players = new HashMap<>();
    /**
     * The name of the host
     */
    public static String host = "";
    /**
     * The name of the lobby
     */
    public static String name = "";
    /**
     * The ip address of the current connection
     */
    public static String ipAddress = "";
    /**
     * The selected mode
     */
    public static GameMode mode = new BattleRoyale();
    /**
     * The selected map
     */
    public static Map map = MapManager.maps.get("X-Factor");
    /**
     * The tick rate of the server
     */
    public static int tickRate;
    /**
     * The tick rate of the server
     */
    public static String player;
    /**
     * Reader for receiving server messages
     */
    private static BufferedReader in;
    /**
     * Writer for communicating with the server
     */
    private static PrintWriter out;
    /**
     * The socket for the server communication
     */
    private static Socket socket;
    /**
     * Event that is triggered whenever a disconnect was recognized
     */
    private static DisconnectEvent disconnectEvent;
    /**
     * Event that is triggered whenever the lobby state changes
     */
    private static LobbyChangeEvent lobbyChangeEvent;
    /**
     * True if the player left on purpose
     */
    private static boolean leave = false;
    /**
     * The state of the current game
     */
    private static GameState gameState;
    /**
     * The game class
     */
    private static Game game;

    /**
     * Try to start a socket connection
     *
     * @param name      name of the lobby
     * @param ipAddress address
     * @param tickRate  of the server
     * @param player    name of the client
     * @throws IOException if the socket fails to connect
     */
    public static void connect(String name, String ipAddress, int tickRate, String player) throws IOException {
        Lobby.name = name;
        Lobby.tickRate = tickRate;
        Lobby.player = player;
        Lobby.ipAddress = ipAddress;

        // Try to build up the connection
        socket = new Socket(ipAddress, Server.GAME_PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Start waiting for messages
        new Thread(Lobby::receive).start();
    }

    /**
     * Set the disconnection event
     *
     * @param disconnectEvent to be triggered
     */
    public static void setDisconnectEvent(DisconnectEvent disconnectEvent) {
        Lobby.disconnectEvent = disconnectEvent;
    }

    /**
     * Set the lobby change event
     *
     * @param lobbyChangeEvent to be triggered
     */
    public static void setLobbyChangeEvent(LobbyChangeEvent lobbyChangeEvent) {
        Lobby.lobbyChangeEvent = lobbyChangeEvent;
    }

    /**
     * Check if a player is host
     *
     * @param player to be checked
     * @return true if player is host
     */
    public static boolean isHost(String player) {
        return player.equals(host);
    }

    /**
     * Promote a player to host status
     *
     * @param player to be promoted
     */
    public static void promoteHost(String player) {
        LobbyState state = new LobbyState(player, mode.name);
        out.println(state.toJson());
    }

    /**
     * Notify the server if the host changed the lobby state
     */
    public static void changeMode(GameMode mode) {
        LobbyState state = new LobbyState(host, mode.name);
        out.println(state.toJson());
    }

    /**
     * Wait for server messages
     */
    private static void receive() {
        while (true) {
            String data;

            // Wait for next message
            try {
                if ((data = in.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                break;
            }

            // Parse the message
            Message message = Message.fromJson(data);
            handleMessage(message);
        }

        // Disconnect
        if (!leave) {
            leave();
            disconnectEvent.onDisconnect("Lost connection to the lobby!");
        }
    }

    /**
     * Leave the lobby
     */
    public static void leave() {
        if (gameState != null) {
            gameState.state = GameState.FINISHED;
        }
        players.clear();
        leave = true;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle a message from the server
     */
    private static void handleMessage(Message message) {
        switch (message.type) {
            case Message.LOBBY_STATE_TYPE:
                detectLobbyChanges((LobbyState) message);
                break;

            case Message.MAP_TYPE:
                startGame((Map) message);
                break;

            case Message.GAME_STATE_TYPE:
                changeGameState((GameState) message);
                break;

            case Message.POSITION_TYPE:
                updatePosition((Position) message);
                break;
        }
    }

    /**
     * Check for changes in the lobby state
     *
     * @param lobby message
     */
    private static void detectLobbyChanges(LobbyState lobby) {
        // Check if a player joined
        for (java.util.Map.Entry<String, Integer> player : lobby.players.entrySet()) {
            if (!players.containsKey(player.getKey())) {
                players.put(player.getKey(), new Player(player.getKey(), player.getValue()));
                lobbyChangeEvent.playerJoined(player.getKey(), player.getValue());
            }
        }

        // Check if a player left
        ArrayList<String> removedPlayers = new ArrayList<>();
        for (java.util.Map.Entry<String, Player> player : players.entrySet()) {
            // Remove the player if he isn't there anymore
            if (!lobby.players.containsKey(player.getKey())) {
                removedPlayers.add(player.getKey());
            }
        }
        for (String player : removedPlayers) {
            players.remove(player);
            lobbyChangeEvent.playerLeft(player);
        }

        // Check if the mode changed
        if (!mode.name.equals(lobby.gameMode)) {
            mode = GameMode.getMode(lobby.gameMode);
            lobbyChangeEvent.gameModeChanged(lobby.gameMode);
        }

        // Check if the host changed
        if (!host.equals(lobby.hostId)) {
            host = lobby.hostId;
            lobbyChangeEvent.hostChanged(host);
        }
    }

    /**
     * Start the game as soon as the server broadcasts the map
     *
     * @param map to play on
     */
    private static synchronized void startGame(Map map) {
        Lobby.map = map;
        game = new Game(player);
        new Thread(() -> MB.show(game, false)).start();
        out.println(GameState.preparing().toJson());
    }

    /**
     * Start the game by sending the selected map
     */
    public static void startGame() {
        out.println(map.toJson());
    }

    /**
     * React to game state changes
     *
     * @param gameState of the game
     */
    private static void changeGameState(GameState gameState) {
        Lobby.gameState = gameState;
        switch (gameState.state) {
            case GameState.RUNNING:
                new Thread(() -> startCountdown(gameState.timestamp + 3000)).start();
                break;

            case GameState.FINISHED:
                leaveGame(gameState.winner);
                break;
        }
    }

    /**
     * Start the game countdown and enable controls when finished
     */
    private static void startCountdown(long timestamp) {
        try {
            // wait until the game loop can start
            long timeDifference = timestamp - System.currentTimeMillis();
            while (timeDifference > 0) {
                MB.activePanel.toastSuccess(
                        "Game starts in " + ((timestamp - System.currentTimeMillis()) / 1000 + 1) + "s!"
                );
                Thread.sleep(timeDifference % 1000 + 1);
                timeDifference = timestamp - System.currentTimeMillis();
            }
            MB.activePanel.toastSuccess("GO!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Start game in new thread
        new Thread(game::startGame).start();

        int waitTime = 1000 / tickRate;
        while (gameState.state == GameState.RUNNING) {
            long startTime = System.currentTimeMillis();

            // Send the players position
            out.println(players.get(player).position.toJson());

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
    }

    /**
     * Update a player's position
     *
     * @param position of the player
     */
    private static void updatePosition(Position position) {
        if (!position.playerId.equals(player)) {
            players.get(position.playerId).position = position;
        }
    }

    /**
     * Leave the game
     */
    private static void leaveGame(String winner) {
        new Thread(() -> {
            try {
                MB.show(new DetailedLobbyView(player, name, ipAddress, tickRate), false);
            } catch (IOException e) {
                MB.show(new ServerView(), false);
                MB.activePanel.toastError("Lost connection to game session!");
            }
            MB.activePanel.toastSuccess(winner + " won the game!");
        });
    }

    /**
     * Disconnect event
     */
    public interface DisconnectEvent {
        void onDisconnect(String parameter);
    }

    /**
     * Lobby change event
     */
    public interface LobbyChangeEvent {
        void playerJoined(String name, int color);

        void playerLeft(String name);

        void hostChanged(String name);

        void gameModeChanged(String gameMode);
    }
}
