package Server;

import Server.Messages.LobbyInfo;
import Server.Messages.Message;
import Server.Messages.ServerInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
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
            httpServer.createContext("/server", new RequestHandler() {
                @Override
                public Message getMessage() {
                    return new ServerInfo(server);
                }
            });
            httpServer.createContext("/lobby", new RequestHandler() {
                @Override
                public Message getMessage() {
                    return new LobbyInfo(server);
                }
            });

            // assign ThreadPool to the HttpServer
            httpServer.setExecutor(threadPool);

            // start the server
            httpServer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static abstract class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            OutputStream outputStream = httpExchange.getResponseBody();

            // generate response from Message object
            String jsonResponse = getMessage().toJson();

            httpExchange.sendResponseHeaders(200, jsonResponse.length());
            outputStream.write(jsonResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }

        /**
         * Generate Message object for the response
         *
         * @return new Message object
         */
        public abstract Message getMessage();
    }
}
