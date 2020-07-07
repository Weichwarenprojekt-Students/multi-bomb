package Server;

import General.MB;
import General.Shared.MBListView;
import Menu.ServerView;

import java.util.ArrayList;

public class ServerList {
    /**
     * List for local found servers
     */
    private final ArrayList<ServerView.ServerListItem> serverList = new ArrayList<>();
    /**
     * Detect server
     */
    private final DetectServer dS = new DetectServer();

    /**
     * Search for servers in local network
     */
    public void searchServers () {
        serverList.clear();

        // Add local servers
        dS.search(serverList);

        // Add remote servers
        MB.settings.remoteServer.forEach(server -> new ScanServerThread(server, serverList, "remote").run());
    }

    /**
     * Update ListView
     */
    public void updateListView (MBListView<ServerView.ServerListItem> listView) {
        listView.addMissingItems(serverList);
    }
}

