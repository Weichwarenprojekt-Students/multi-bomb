package Server;

import Server.Messages.Message;
import Server.Messages.REST.ServerInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class ScanServerThread implements Runnable {
    /**
     * ArrayList for detected servers
     */
    public final HashMap<String, ServerInfo> serverList;
    /**
     * httpClient for request
     */
    public final HttpClient httpClient = HttpClient.newBuilder().build();
    /**
     * The server address
     */
    public String serverAddress;
    /**
     * Type of server
     */
    public String type;

    public ScanServerThread(String address, HashMap<String, ServerInfo> serverList, String type) {
        this.serverAddress = address;
        this.serverList = serverList;
        this.type = type;
    }

    @Override
    public void run() {
        // Create http request for server info
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create("http://" + serverAddress + ":" + Server.HTTP_PORT + "/server")
        ).build();

        try {
            // Send request
            HttpResponse<String> response;
            try {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                return;
            }

            // Add the item
            Message responseMessage = Message.fromJson(response.body());
            ServerInfo serverInfo = (ServerInfo) responseMessage;
            serverInfo.serverType = type;
            synchronized (serverList) {
                serverList.put(serverAddress, serverInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}