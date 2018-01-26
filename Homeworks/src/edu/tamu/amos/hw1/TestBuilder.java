package edu.tamu.amos.hw1;

import org.jnetpcap.Pcap;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Amos on 2018/1/22.
 */
public class TestBuilder {


    public static void main(String[] args) {
        // Start point of jNetPCap is class Pcap
        // Pcap stands for Pcapture ?
        // Main Static Methods:
        // openLive: Live Network
        // openOffline: From File
        // openDead: Dummy Pcap Session

        final StringBuilder errorBuffer = new StringBuilder(); // For any error msgs

        if (args.length != 1) {
            System.err.println("Please type the relative location of pcap file.");
            return;
        }
        final String fileName = args[0];

        final Pcap pcap = Pcap.openOffline(fileName, errorBuffer);

        // Error Checking
        if (pcap == null) {
            System.err.print("Error while opening device for capture: " + errorBuffer.toString());
            return;
        }

        HTFPacketHandler packetHandler = new HTFPacketHandler();
        Map<String, TreeMap<Long, Session>> holder = new HashMap<>();

        try {
            Util.showNotes();

            // statusCode:  -1 on ERROR     0 on cnt exhausted      -2 on pcap_breakloop() call
            int statusCode = pcap.loop(Integer.MAX_VALUE, packetHandler, holder);
            if (statusCode != 0) {
                System.err.print("Error while processing packets");
            }

            for(String connectionPairs : holder.keySet()) {
                TreeMap<Long, Session> connectionsMap = holder.get(connectionPairs);

                for(Long sessionStartTime: connectionsMap.keySet()) {
                    Session instance = connectionsMap.get(sessionStartTime);
                    switch (instance.getApplicationType()) {
                        case "FTP":
                            Util.prettyPrintFTPSession(instance);
                            break;
                        case "TELNET":
                            Util.prettyPrintTelnetSession(instance);
                            break;
                        case "HTTP":
                            Util.prettyPrintHTTPSession(instance);
                            break;
                        default:

                            break;
                    }
                }
            }

        } finally {
            pcap.close();
        }

//        System.out.println("Successfully opened local file: " +fileName);
    }
}