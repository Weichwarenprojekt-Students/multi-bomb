package Server;

import Server.Messages.REST.LobbyInfo;
import Server.Messages.Message;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DetectLobby {

    /**
     * Get LobbyInfo from server
     */
    public static LobbyInfo getLobbyInfo (String serverAddress) {

        //Create HttpClient and HttpRequest
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://" + serverAddress + ":" + Server.HTTP_PORT +"/lobby")).build();
        LobbyInfo lobbyInfo;


        //Send HttpRequest and return Lobbyinfo message
        try {
            HttpResponse<String> response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
            Message responseMessage = Message.fromJson(response.body());
            lobbyInfo = (LobbyInfo) responseMessage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lobbyInfo;
    }
}