package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryThread implements Runnable {

    /**
     * The request message
     */
    public static String UDP_REQUEST = "BROADCAST REQUEST";
    /**
     * The response message
     */
    public static String UDP_RESPONSE = "BROADCAST RESPONSE";
    /**
     * The used port
     */
    public static int PORT = Server.UDP_PORT;

    @Override
    public void run() {
        try {
            // Listen for udp broadcasts
            DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            // Start the server
            while (true) {
                // Receive a packet
                byte[] receiveBuffer = new byte[15000];
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);

                // Extract the message
                String message = new String(packet.getData());

                // Check if package holds right message
                if (message.contains(UDP_REQUEST)) {
                    byte[] response = UDP_RESPONSE.getBytes();

                    // Send the response
                    DatagramPacket sendPacket = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}