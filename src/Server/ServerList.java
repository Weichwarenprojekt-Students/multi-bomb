package Server;

import General.MB;
import Server.Messages.REST.ServerInfo;

import java.util.HashMap;

public class ServerList {
    /**
     * List for local found servers
     */
    public final HashMap<String, ServerInfo> servers = new HashMap<>();
    /**
     * Detect server
     */
    private final DetectServer dS = new DetectServer();

    /**
     * Search for servers in local network
     */
    public void searchServers () {
        servers.clear();

        // Add local servers
        dS.search(servers);

        // Add remote servers
        MB.settings.remoteServers.forEach(server -> new ScanServerThread(server, servers, "Remote").run());
    }
}

