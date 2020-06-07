package Server;

import Server.Messages.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpThread extends Thread {
    /**
     * Instance of the game server
     */
    public final Server server;

    /**
     * Constructor
     *
     * @param server object about which information is provided by the HttpServer
     */
    public HttpThread(Server server) {
        this.server = server;
    }

    /**
     * Run the HTTP server which provides information about the game server
     */
    @Override
    public void run() {
        try {
            // create new HttpServer
            HttpServer httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", Server.HTTP_PORT), 0);

            // create new ThreadPoolExecutor for the HttpServer
            ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

            // add handlers for two routes /server and /lobby
            httpServer.createContext("/server", new ServerRequestHandler());
            httpServer.createContext("/lobby", new LobbyRequestHandler());

            // assign ThreadPool to the HttpServer
            httpServer.setExecutor(threadPool);

            // start the server
            httpServer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerRequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            int code;
            String responseString;

            if (httpExchange.getRequestMethod().equals("GET")) {
                code = 200; // OK

                // generate response from Message object
                responseString = getMessage().toJson();

            } else {
                code = 405; // Method Not Allowed
                responseString = new ErrorMessage("Method Not Allowed").toJson();
            }

            byte[] response = responseString.getBytes();
            httpExchange.sendResponseHeaders(code, response.length);

            OutputStream os = httpExchange.getResponseBody();
            os.write(response);
            os.close();
        }

        /**
         * Generate Message object for the response
         *
         * @return new Message object
         */
        public Message getMessage() {
            return new ServerInfo(server);
        }

    }

    public class LobbyRequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            int code;
            String responseString;
            if (httpExchange.getRequestMethod().equals("GET")) {
                code = 200; // OK
                // generate response from Message object
                responseString = getMessage().toJson();

            } else if (httpExchange.getRequestMethod().equals("POST")) {
                BufferedReader reqBody = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));

                Message msg = Message.fromJson(reqBody.readLine());

                if (msg.type.equals(Message.JOIN_LOBBY_TYPE)) {
                    JoinLobby joinLobby = (JoinLobby) msg;
                    String lobbyName = joinLobby.lobbyName;
                    String playerID = joinLobby.playerID;

                    String remoteIp = httpExchange.getRequestHeaders().getFirst("X-FORWARDED-FOR");
                    if (remoteIp == null) {
                        remoteIp = httpExchange.getRemoteAddress().getAddress().getHostAddress();
                    }

                    ErrorMessage errorMessage = server.prepareNewPlayer(remoteIp, lobbyName, playerID);

                    if (errorMessage == null) {
                        code = 200; // OK
                        responseString = "";
                    } else {
                        code = 400; // Bad Request
                        responseString = errorMessage.toJson();
                    }

                } else if (msg.type.equals(Message.CREATE_LOBBY_TYPE)) {
                    CreateLobby createLobby = (CreateLobby) msg;
                    String lobbyName = createLobby.lobbyName;
                    String playerID = createLobby.playerID;

                    String remoteIp = httpExchange.getRequestHeaders().getFirst("X-FORWARDED-FOR");
                    if (remoteIp == null) {
                        remoteIp = httpExchange.getRemoteAddress().getAddress().getHostAddress();
                    }

                    ErrorMessage errorMessage = server.createLobby(lobbyName);

                    if (errorMessage != null) {
                        // Error with creating lobby
                        code = 400; // Bad Request
                        responseString = errorMessage.toJson();

                    } else {
                        errorMessage = server.prepareNewPlayer(remoteIp, lobbyName, playerID);

                        if (errorMessage == null) {
                            code = 200; // OK
                            responseString = "";
                        } else {
                            // Error with joining lobby
                            code = 400; // Bad Request
                            responseString = errorMessage.toJson();

                            server.closeLobby(lobbyName);
                        }

                    }
                } else {
                    code = 400; // Bad Request
                    responseString = new ErrorMessage("JSON message not recognized").toJson();
                }
            } else {
                code = 405; // Method Not Allowed
                responseString = new ErrorMessage("Method Not Allowed").toJson();
            }

            byte[] response = responseString.getBytes();
            httpExchange.sendResponseHeaders(code, response.length);

            OutputStream os = httpExchange.getResponseBody();
            os.write(response);
            os.close();

        }

        /**
         * Generate Message object for the response
         *
         * @return new Message object
         */
        public Message getMessage() {
            return new LobbyInfo(server);
        }
    }
}
