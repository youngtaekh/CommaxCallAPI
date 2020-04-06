package io.dotconnect.p2p.utils;

import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.dotconnect.p2p.utils.Configuration.*;
import static io.dotconnect.p2p.utils.Configuration.turnUserPassword;

public class Ice {
    public List<PeerConnection.IceServer> getIceServers() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        PeerConnection.IceServer stunServer =
                PeerConnection.IceServer.builder("stun:" + stunServerAddress + ":" + stunServerPort)
                        .setUsername("")
                        .setPassword("")
                        .createIceServer();
        PeerConnection.IceServer turnServer =
                PeerConnection.IceServer.builder("turn:" + turnServerAddress + ":" + turnServerPort) //  + "?transport=tcp"
                        .setUsername(turnUserId)
                        .setPassword(turnUserPassword)
                        .createIceServer();
        iceServers.add(stunServer);
        iceServers.add(turnServer);

        return iceServers;
    }

    public String addCandidate(String sdpDescription, String candidate) {
        final String[] lines = sdpDescription.split("\r\n");

        List<String> lineList = new ArrayList<>();
        List<Integer> lineIndex = new ArrayList<>();
        for (int i=0;i<lines.length;i++) {
            String line = lines[i];
            lineList.add(line);
            if (line.startsWith("a=ice-ufrag")) {
                lineIndex.add(i);
            }
        }

        for (int i=lineIndex.size()-1;i>=0;i--) {
            lineList.add(lineIndex.get(i), "a=" + candidate);
        }

        return joinString(lineList, "\r\n", true /* delimiterAtEnd */);
    }

    private static String joinString(
            Iterable<? extends CharSequence> s, String delimiter, boolean delimiterAtEnd) {
        Iterator<? extends CharSequence> iter = s.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }
        if (delimiterAtEnd) {
            buffer.append(delimiter);
        }
        return buffer.toString();
    }
}
