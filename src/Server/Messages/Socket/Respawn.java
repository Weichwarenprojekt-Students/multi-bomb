package Server.Messages.Socket;

import Game.Models.Direction;

public class Respawn extends Position {
    public final String type = RESPAWN_TYPE;

    public Respawn(Position pos) {
        // Copy important values from position
        x = pos.x;
        y = pos.y;
        playerId = pos.playerId;

        // set other values to sane defaults
        direction = Direction.SOUTH;
        moving = false;
    }
}
