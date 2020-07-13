package Game.Items;

import Game.Game;
import Game.Models.Field;
import Game.Models.Player;
import Game.Models.Upgrades;
import General.MultiBomb;
import Server.Messages.Socket.ItemAction;
import Game.Lobby;

import java.awt.*;
import java.util.Random;

public class Teleport extends Item {

    /**
     * The time it takes to teleport
     */
    public static long PREPARATION_TIME = 500;
    /**
     * The random object
     */
    private static final Random random = new Random();

    /**
     * Constructor
     */
    public Teleport() {
        super(Item.TELEPORT, Field.TELEPORT);
        ammunition = 1;
    }

    @Override
    public boolean isUsable(int m, int n, Upgrades upgrades) {
        return false;
    }

    @Override
    public Item use(ItemAction action, Player player) {
        // Start to fade out the player
        MultiBomb.startTimedAction(Game.WAIT_TIME, ((deltaTime, totalTime) -> {
            // Set the opacity
            player.opacity = (float) (PREPARATION_TIME - totalTime) / PREPARATION_TIME;
            if (player.opacity < 0) {
                player.opacity = 0;
            }

            if (totalTime > PREPARATION_TIME) {
                // Choose a random player and change the position
                if (!player.fadingOut && player.name.equals(Lobby.player)) {
                    // Choose the player
                    Player nextPlayer = null;
                    Object[] players = Lobby.players.values().toArray();
                    while (nextPlayer == null || nextPlayer.name.equals(player.name)) {
                        nextPlayer = (Player) players[random.nextInt(players.length)];
                    }

                    // Go to the position
                    player.position.x = nextPlayer.position.x;
                    player.position.y = nextPlayer.position.y;
                }
                // Start to fade in the player
                fadeIn(player);

                // Stop the player
                return false;
            }
            return true;
        }));

        return new Teleport();
    }

    /**
     * Fade in the player after the position change
     *
     * @param player to be faded out
     */
    private void fadeIn(Player player) {
        // Start to fade in the player
        MultiBomb.startTimedAction(Game.WAIT_TIME, ((deltaTime, totalTime) -> {
            player.opacity = (float) totalTime / PREPARATION_TIME;
            if (player.opacity > 1) {
                player.opacity = 1;
            }
            return totalTime <= PREPARATION_TIME;
        }));
    }

    @Override
    public Item draw(Graphics2D g, int m, int n) {
        return null;
    }
}
