package Menu.Models;

import Game.GameModes.BattleRoyale;
import Game.GameModes.GameMode;
import Game.Models.Map;
import General.MB;

import java.awt.*;
import java.util.HashMap;

public class Lobby {
    /**
     * The name of the host
     */
    public String host;
    /**
     * The name of the lobby
     */
    public String name;
    /**
     * The selected mode
     */
    public GameMode mode = new BattleRoyale();
    /**
     * The selected mode
     */
    public Map map = new Map();
    /**
     * The players
     */
    public HashMap<String, Color> players = new HashMap<>();

    /**
     * Constructor
     */
    public Lobby(String name, String host) {
        this.name = name;
        this.host = host;
    }

    /**
     * Check if a player is host
     *
     * @param player to be checked
     * @return true if player is host
     */
    public boolean isHost(String player) {
        return player.equals(host);
    }

    /**
     * Promote a player to host status
     *
     * @param player to be promoted
     */
    public void promoteHost(String player) {
        host = player;
        MB.activePanel.toastSuccess(player + " was", "promoted to host!");
    }
}
