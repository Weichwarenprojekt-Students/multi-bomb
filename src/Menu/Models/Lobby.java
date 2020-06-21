package Menu.Models;

import Game.GameModes.BattleRoyale;
import Game.GameModes.GameMode;
import Game.Models.Map;
import Game.Models.Player;
import Server.Messages.Message;
import Server.Messages.Socket.LobbyState;
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
     * The selected mode
     */
    public static GameMode mode = new BattleRoyale();
    /**
     * The selected mode
     */
    public static Map map = new Map();
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
     * Try to start a socket connection
     *
     * @param name name of the lobby
     * @param ip   address
     * @throws IOException if the socket fails to connect
     */
    public static void connect(String name, String ip) throws IOException {
        Lobby.name = name;

        // Try to build up the connection
        socket = new Socket(ip, Server.GAME_PORT);
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
        leave = true;
        players.clear();
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
