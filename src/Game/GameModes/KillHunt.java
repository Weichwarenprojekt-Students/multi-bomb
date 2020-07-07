package Game.GameModes;

import Game.Models.Field;
import Server.Messages.Message;
import Server.Messages.Socket.Position;
import Server.Messages.Socket.Respawn;
import Server.Models.Player;

import java.util.ArrayList;
import java.util.List;
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
    public synchronized List<Message> handleHit(Player player, Player from, Position spawnPoint) {
        List<Message> result = new ArrayList<>();

        // player got hit and respawns
        spawnPoint.playerId = player.name;
        result.add(new Respawn(spawnPoint));

        if (!player.name.equals(from.name)) {
            // update kill for other player
            from.playerState.kills++;
            result.add(from.playerState);
        }

        return result;
    }
}
