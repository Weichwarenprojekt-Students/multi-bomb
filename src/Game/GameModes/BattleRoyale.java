package Game.GameModes;

import Game.Models.Field;
import Server.Messages.Message;
import Server.Messages.Socket.PlayerState;
import Server.Models.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static General.MultiBomb.LOGGER;

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
        // Get list of all players that are alive
        List<PlayerState> alivePlayers = players.values().stream().filter(PlayerState::isAlive)
                .collect(Collectors.toList());

        // If only one player is alive
        if (alivePlayers.size() == 1) {
            // Return a non-empty Optional<String> containing the playerId of the winner
            return alivePlayers.stream().findFirst().map(ps -> ps.playerId);
        }

        // Return empty Optional because there is no winner yet
        return Optional.empty();
    }

    @Override
    public synchronized List<Message> handleHit(Player player, Player from) {
        List<Message> result = new ArrayList<>();

        if (player.isAlive()) {
            // player got hit
            player.hit();
            // notify all players about the hit
            result.add(player.playerState);

            LOGGER.info(String.format("Player %s got hit by %s", player.name, from.name));

            if (!player.isAlive() && !player.name.equals(from.name)) {
                // player died and the hit was from another player
                from.playerState.kills++;
                // notify players about the kill
                result.add(from.playerState);

                LOGGER.info(String.format("Player %s killed %s", from.name, player.name));
            }
        }

        return result;
    }
}
