package Server;

import Menu.ServerView;
import Server.Messages.Message;
import Server.Messages.REST.ServerInfo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class ScanServerThread implements Runnable {
    /**
     * The server address
     */
    public String serverAddress;
    /**
     * Type of server
     */
    public String type;
    /**
     * ArrayList for detected servers
     */
    final public ArrayList<ServerView.ServerListItem> serverList;
    /**
     * httpClient for request
     */
    public final HttpClient httpClient = HttpClient.newBuilder().build();

    public ScanServerThread(String address, ArrayList<ServerView.ServerListItem> serverList, String type) {
        this.serverAddress = address;
        this.serverList = serverList;
        this.type = type;
    }

    @Override
    public void run() {
        // Create http request for server info
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create("http://" + serverAddress + ":" + Server.HTTP_PORT +"/server")
        ).build();

        try {
            // Send request
            HttpResponse<String> response;
            try {
                 response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                return;
            }

            // Store response message
            Message responseMessage = Message.fromJson(response.body());


            // Cast response message to ServerList
            ServerInfo serverInfo = (ServerInfo) responseMessage;
            String description = "Tick-Rate " + serverInfo.ticksPerSecond + " - Lobbies " + serverInfo.lobbyCount + "/" + serverInfo.maxLobbies + " - Type " + type;
            ServerView.ServerListItem server = new ServerView.ServerListItem(serverInfo.name, description, serverAddress);

            // Add detected server to serverlist
            synchronized (serverList) {
                serverList.add(server);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}