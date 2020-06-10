package Server;

import Server.Messages.Socket.CloseConnection;
import Server.Messages.ErrorMessage;
import Server.Messages.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerConnection extends Thread {
    /**
     * Name of the player
     */
    public final String name;
    /**
     * TCP socket connection to the client
     */
    private final Socket socket;
    /**
     * Lobby the player is in
     */
    private final Lobby lobby;
    /**
     * Output stream
     */
    private final PrintWriter out;
    /**
     * Input stream
     */
    private final BufferedReader in;
    /**
     * Color of the player
     */
    public int color;
    /**
     * Indicate if PlayerConnection is still alive
     */
    private boolean alive;

    /**
     * Constructor
     *
     * @param socket     socket connection with the client
     * @param lobby      lobby the player is in
     * @param playerName name of the player
     * @throws IOException if there are problems with the socket or the in-/output streams
     */
    public PlayerConnection(Socket socket, Lobby lobby, String playerName) throws IOException {
        this.socket = socket;
        this.lobby = lobby;
        this.name = playerName;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            socket.close();
            throw e;
        }
    }

    @Override
    public void run() {
        alive = lobby.addPlayer(this);

        if (!alive && lobby.getPlayerColors().containsKey(name)) {
            send(new ErrorMessage("Name already taken, please choose a different one!"));
        }

        String jsonMessage;
        while (alive) {
            try {
                synchronized (in) {
                    // readLine() return null if socket connection was closed from other end
                    if ((jsonMessage = in.readLine()) == null) break;
                }
            } catch (IOException e) {
                break;
            }

            Message message = Message.fromJson(jsonMessage);

            handleMessage(message);
        }

        send(new CloseConnection());

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lobby.removePlayer(this);
    }

    /**
     * Handle incoming message
     *
     * @param msg message to handle
     */
    private synchronized void handleMessage(Message msg) {
    }

    /**
     * Send message
     *
     * @param message message to send
     */
    public synchronized void send(Message message) {
        out.println(message.toJson());
    }
}
