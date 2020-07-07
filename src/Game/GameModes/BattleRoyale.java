package Game.GameModes;

import Game.Models.Field;
import Server.Messages.Socket.PlayerState;
import Server.Messages.Socket.Position;
import Server.Models.Player;

import java.util.Optional;
import java.util.stream.Stream;

public class BattleRoyale extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "The last man standing will be the winner!";

    /**
     * Constructor
     */
    public BattleRoyale() {
        super(GameMode.BATTLE_ROYALE, DESCRIPTION, Field.getAllItems());
    }

    @Override
    public void updateClientState() {

    }

    @Override
    public synchronized Optional<String> calculateWinner() {
        // Get stream of all players that are alive
        Stream<PlayerState> alivePlayerStream = players.values().stream().filter(PlayerState::isAlive);

        // If only one player is alive
        if (alivePlayerStream.count() == 1) {
            // Return a non-empty Optional<String> containing the playerId of the winner
            return alivePlayerStream.findFirst().map(ps -> ps.playerId);
        }

        // Return empty Optional because there is no winner yet
        return Optional.empty();
    }

    @Override
    public synchronized void handleDeath(Player player, Position spawnPoint) {
        // Deaths are not handled in a special way
    }
}
