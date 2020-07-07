package Game.GameModes;

import Game.Models.Field;
import Server.Messages.Socket.Map;
import Server.Messages.Socket.PlayerState;
import Server.Messages.Socket.Position;
import Server.Models.Player;

import java.util.Optional;

public class KillHunt extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "The first one to get 10 kills is the winner!";

    /**
     * Constructor
     */
    public KillHunt() {
        super(GameMode.KILL_HUNT, DESCRIPTION, Field.getAllItems());
    }

    @Override
    public void updateClientState() {

    }

    @Override
    public synchronized Optional<String> calculateWinner() {
        // Return the playerId of the first player to reach 10 kills, empty Optional if no player has 10 kills yet
        return players.values().stream().filter(ps -> ps.kills >= 10).findFirst().map(ps -> ps.playerId);
    }

    @Override
    public synchronized void handleDeath(Player player, Position spawnPoint) {
        // Restore the health of the player
        player.playerState.health = PlayerState.DEFAULT_HEALTH;

        // Set their new position to the original spawn point
        player.position = new Position(
                spawnPoint.x * Map.FIELD_SIZE + (float) Map.FIELD_SIZE / 2,
                spawnPoint.y * Map.FIELD_SIZE + (float) Map.FIELD_SIZE / 2
        );
    }
}
