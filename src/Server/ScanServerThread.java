package Server;

import Menu.ServerView;
import Server.Messages.Message;
import Server.Messages.ServerInfo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class ScanServerThread implements  Runnable{
    /**
     * The server address
     */
    public String serverAddress;
    /**
     * ArrayList for detected servers
     */
    public ArrayList<ServerView.ServerListItem> serverList;
    /**
     * httpClient for request
     */
    public final HttpClient httpClient = HttpClient.newBuilder().build();

    public ScanServerThread(String address, ArrayList<ServerView.ServerListItem> serverList) {
        this.serverAddress = address;
        this.serverList = serverList;
    }

    @Override
    public void run() {
        //Create http Request for Serverinfo
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + serverAddress + ":" + Server.HTTP_PORT +"/server")).build();

        try {
            //send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            //Store responsemessage
            Message responseMessage = Message.fromJson(response.body());


            //Cast responsemessage to ServerList
            ServerInfo serverInfo = (ServerInfo) responseMessage;


            String description = "Tickrate " + serverInfo.ticksPerSecond + " - Lobbies " + serverInfo.lobbyCount + "/" + serverInfo.maxLobbies + " - Type Local";
            ServerView.ServerListItem server = new ServerView.ServerListItem(serverInfo.name, description, serverAddress);

            //add detected server to serverlist
            serverList.add(server);


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}