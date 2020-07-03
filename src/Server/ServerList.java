package Server;

import General.MB;
import General.Shared.MBListView;
import Menu.ServerView;

import java.util.ArrayList;

public class ServerList {
    /**
     * List for local found servers
     */
    ArrayList<ServerView.ServerListItem> localServerList = new ArrayList<>();
    /**
     * List for remote added servers
     */
    public ArrayList<ServerView.ServerListItem> remoteServerList = new ArrayList<>();
    /**
     * List that contains all servers
     */
    public ArrayList<ServerView.ServerListItem> allServerList = new ArrayList<>();
    /**
     *
     */
    DetectServer ds = new DetectServer();

    public ServerList() {
        MB.settings.remoteServer.forEach(server -> new Thread(new ScanServerThread(server, remoteServerList, "remote")).start());
    }

    public void updateAllServerList () {
        allServerList.clear();
        allServerList.addAll(localServerList);
        allServerList.addAll(remoteServerList);
    }

    public void searchLocalServer () {
        localServerList.clear();
        ds.search(localServerList);
    }

    public void updateListView (MBListView<ServerView.ServerListItem> listView) {
        listView.removeAllItems();
        allServerList.forEach(listView::addItem);
    }

    public void addRemoteServer (String serverAddress) {
        MB.settings.remoteServer.add(serverAddress);
        MB.settings.saveSettings();
        new Thread(new ScanServerThread(serverAddress, remoteServerList, "remote")).start();
    }
}

