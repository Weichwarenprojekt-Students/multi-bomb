package Server.Messages;

import Server.Lobby;
import Server.Server;

public class LobbyInfo extends Message {
    /**
     * Information of all lobbies
     */
    public SingleLobbyInfo[] lobbies;

    /**
     * Constructor
     *
     * @param server game server object
     */
    public LobbyInfo(Server server) {
        // Initialize message with type
        super(Message.LOBBY_INFO_TYPE);

        // transform all lobbies from server into SingleLobbyInfo objects
        lobbies = server.getLobbies().stream().map(SingleLobbyInfo::new).toArray(SingleLobbyInfo[]::new);
    }

    /**
     * Class representing the information of one lobby
     */
    public static class SingleLobbyInfo {
        /**
         * Possible states of a lobby that are displayed in lobby overview
         */
        public static final String IN_LOBBY = "In Lobby", IN_GAME = "In Game";
        /**
         * Name of the lobby
         */
        public String name;
        /**
         * Number of players in the lobby
         */
        public int players;
        /**
         * Name of the game mode
         */
        public String gameMode;
        /**
         * Status of the lobby, either value of IN_LOBBY or IN_GAME
         */
        public String status;

        /**
         * Constructor
         *
         * @param lobby o
         */
        public SingleLobbyInfo(Lobby lobby) {
            name = lobby.name;
            players = lobby.getPlayerColors().size();
            gameMode = lobby.gameMode;
            status = (lobby.state == Lobby.WAITING) ? IN_LOBBY : IN_GAME;
        }
    }
}
