package Game.GameModes;

import Game.Models.Field;
import Server.Messages.Socket.PlayerState;
import Server.Messages.Socket.Position;
import Server.Models.Player;

import java.util.Optional;
import java.util.stream.Stream;

public class Classic extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "No flamethrower, no other items, no bullshit. Bombs only!";

    /**
     * Constructor
     */
    public Classic() {
        super(GameMode.CLASSIC, DESCRIPTION, Field.BOMB.id, Field.HEART.id, Field.EXPLOSION.id, Field.SPEED.id);
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
