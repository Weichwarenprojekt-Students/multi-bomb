package Server;

import General.MB;
import General.Shared.MBListView;
import Menu.ServerView;

import java.util.ArrayList;

public class ServerList {
    /**
     * List for local found servers
     */
    private final ArrayList<ServerView.ServerListItem> localServerList = new ArrayList<>();
    /**
     * List for remote added servers
     */
    private final ArrayList<ServerView.ServerListItem> remoteServerList = new ArrayList<>();
    /**
     * List that contains all servers
     */
    private final ArrayList<ServerView.ServerListItem> allServerList = new ArrayList<>();
    /**
     * Detectserver
     */
    private DetectServer ds = new DetectServer();

    public ServerList() {
        MB.settings.remoteServer.forEach(server -> new Thread(new ScanServerThread(server, remoteServerList, "remote")).start());
    }

    /**
     * Add all found servers to allServerList
     */
    public void updateAllServerList () {
        allServerList.clear();
        allServerList.addAll(localServerList);
        allServerList.addAll(remoteServerList);
    }

    /**
     * Search for servers in local network
     */
    public void searchLocalServer () {
        localServerList.clear();
        ds.search(localServerList);
    }

    /**
     * Update ListView
     */
    public void updateListView (MBListView<ServerView.ServerListItem> listView) {
        listView.addMissingItems(allServerList);
    }

    /**
     * Add server address to settings
     * Scan remote server
     */
    public void addRemoteServer (String serverAddress) {
        MB.settings.remoteServer.add(serverAddress);
        MB.settings.saveSettings();
        new Thread(new ScanServerThread(serverAddress, remoteServerList, "remote")).start();
    }
}

