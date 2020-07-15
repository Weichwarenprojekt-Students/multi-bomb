package Server;

import Server.Messages.ErrorMessage;
import Server.Messages.Message;
import Server.Messages.REST.CreateLobby;
import Server.Messages.REST.JoinLobby;
import Server.Messages.REST.LobbyInfo;
import Server.Messages.REST.ServerInfo;
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

import static General.MultiBomb.LOGGER;

public class HttpThread extends Thread {
    /**
     * Instance of the game server
     */
    public final Server server;
    /**
     * The actual http server
     */
    private HttpServer httpServer;

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
        LOGGER.config(String.format("Entering: %s %s", HttpThread.class.getName(), "run()"));

        try {
            // create new HttpServer
            httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", Server.HTTP_PORT), 0);

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

        LOGGER.config(String.format("Exiting: %s %s", HttpThread.class.getName(), "run()"));
    }

    /**
     * Close the http server
     */
    public void close() {
        LOGGER.config(String.format("Entering: %s %s", HttpThread.class.getName(), "close()"));
        httpServer.stop(0);
        LOGGER.config(String.format("Exiting: %s %s", HttpThread.class.getName(), "close()"));
    }

    public class ServerRequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            LOGGER.config(String.format("Entering: %s %s", ServerRequestHandler.class.getName(), "handle()"));

            int code;
            String responseString;

            if (httpExchange.getRequestMethod().equals("GET")) {
                code = 200; // OK

                // generate response from Message object
                responseString = new ServerInfo(server).toJson();

            } else {
                code = 405; // Method Not Allowed
                responseString = new ErrorMessage("Method Not Allowed").toJson();
            }

            byte[] response = responseString.getBytes();
            httpExchange.sendResponseHeaders(code, response.length);

            OutputStream os = httpExchange.getResponseBody();
            os.write(response);
            os.close();

            LOGGER.config(String.format("Exiting: %s %s", ServerRequestHandler.class.getName(), "handle()"));
        }
    }

    public class LobbyRequestHandler implements HttpHandler {
        private HttpExchange httpExchange;

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            LOGGER.config(String.format("Entering: %s %s", LobbyRequestHandler.class.getName(), "handle()"));

            this.httpExchange = httpExchange;

            if (httpExchange.getRequestMethod().equals("GET")) {
                sendResponse(200, new LobbyInfo(server).toJson()); // OK
            } else if (httpExchange.getRequestMethod().equals("POST")) {
                handlePost();
            } else {
                sendResponse(405, new ErrorMessage("Method Not Allowed").toJson());
            }

            LOGGER.config(String.format("Exiting: %s %s", LobbyRequestHandler.class.getName(), "handle()"));
        }

        /**
         * Handle post requests to /lobby
         */
        private void handlePost() throws IOException {
            LOGGER.config(String.format("Entering: %s %s", LobbyRequestHandler.class.getName(), "handlePost()"));

            BufferedReader reqBody = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));

            Message msg = Message.fromJson(reqBody.readLine());

            if (msg.type.equals(Message.JOIN_LOBBY_TYPE)) {
                JoinLobby joinLobby = (JoinLobby) msg;
                handleJoin(joinLobby);

            } else if (msg.type.equals(Message.CREATE_LOBBY_TYPE)) {
                CreateLobby createLobby = (CreateLobby) msg;
                handleCreate(createLobby);

            } else {
                sendResponse(400, new ErrorMessage("JSON message not recognized").toJson());
            }

            LOGGER.config(String.format("Exiting: %s %s", LobbyRequestHandler.class.getName(), "handlePost()"));
        }

        /**
         * Handle JoinLobby messages that are POSTed to /lobby
         *
         * @param joinLobby message from the POST request body
         */
        private void handleJoin(JoinLobby joinLobby) throws IOException {
            LOGGER.config(String.format("Entering: %s %s", LobbyRequestHandler.class.getName(), "handleJoin()"));

            String lobbyName = joinLobby.lobbyName;
            String playerID = joinLobby.playerID;

            String remoteIp = httpExchange.getRequestHeaders().getFirst("X-FORWARDED-FOR");
            if (remoteIp == null) {
                remoteIp = httpExchange.getRemoteAddress().getAddress().getHostAddress();
            }

            ErrorMessage errorMessage = server.prepareNewPlayer(remoteIp, lobbyName, playerID);

            if (errorMessage == null) {
                sendResponse(200, ""); // OK
            } else {
                sendResponse(400, errorMessage.toJson()); // Bad Request
            }

            LOGGER.config(String.format("Exiting: %s %s", LobbyRequestHandler.class.getName(), "handleJoin()"));
        }

        /**
         * Handle CreateLobby messages that are POSTed to /lobby
         *
         * @param createLobby message from the POST request body
         */
        private void handleCreate(CreateLobby createLobby) throws IOException {
            LOGGER.config(String.format("Entering: %s %s", LobbyRequestHandler.class.getName(), "handleCreate()"));

            String lobbyName = createLobby.lobbyName;
            String playerID = createLobby.playerID;

            String remoteIp = httpExchange.getRequestHeaders().getFirst("X-FORWARDED-FOR");
            if (remoteIp == null) {
                remoteIp = httpExchange.getRemoteAddress().getAddress().getHostAddress();
            }

            ErrorMessage errorMessage = server.createLobby(lobbyName);

            if (errorMessage != null) {
                // Error with creating lobby
                sendResponse(400, errorMessage.toJson()); // Bad Request

            } else {
                errorMessage = server.prepareNewPlayer(remoteIp, lobbyName, playerID);

                if (errorMessage == null) {
                    sendResponse(200, ""); // OK
                } else {
                    // Error with joining lobby
                    sendResponse(400, errorMessage.toJson());

                    server.closeLobby(lobbyName);
                }

            }

            LOGGER.config(String.format("Exiting: %s %s", LobbyRequestHandler.class.getName(), "handleCreate()"));
        }

        /**
         * Send http response to the requester
         *
         * @param code           http status code for the response
         * @param responseString body of the response
         */
        private void sendResponse(int code, String responseString) throws IOException {
            LOGGER.config(String.format("Entering: %s %s", LobbyRequestHandler.class.getName(), "sendResponse()"));

            byte[] response = responseString.getBytes();
            httpExchange.sendResponseHeaders(code, response.length);

            OutputStream os = httpExchange.getResponseBody();
            os.write(response);
            os.close();

            LOGGER.config(String.format("Exiting: %s %s", LobbyRequestHandler.class.getName(), "sendResponse()"));
        }
    }
}
