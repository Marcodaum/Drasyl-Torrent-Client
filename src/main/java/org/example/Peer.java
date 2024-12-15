package org.example;

import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drasyl.node.event.NodeUpEvent;
import org.example.files.TorrentFile;
import org.example.files.TrackerFile;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;


public class Peer {

    private DrasylNode createNode(int peerId, DrasylConfig peerConfig, TorrentFile torrentFile) throws DrasylException {
        DrasylNode node = new DrasylNode(peerConfig) {

            private TrackerFile fileData = null;

            public void askTrackerForFile(String filename, String TrackerId) {
                send(TrackerId, filename);
            }

            public void receiveFile(String filename) {

            }

            public void publishFile(String filename) {

            }

            @Override
            public void onEvent(final Event event) {
                if (event instanceof NodeUpEvent) {
                    System.out.println("Peer " + peerId + " on. Requesting file information from tracker...");
                    askTrackerForFile(torrentFile.getFilename(), torrentFile.getTrackerId());
                }
                if (event instanceof MessageEvent) {
                    String payload = ((String)((MessageEvent) event).getPayload());
                    int payloadDivider = payload.indexOf(",");
                    String messageReason = payload.substring(0, payloadDivider);
                    String messageContent = payload.substring(payloadDivider + 1);
                    switch (messageReason) {
                        case "fileInfo":
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                fileData = objectMapper.readValue(messageContent, TrackerFile.class);
                                System.out.println(fileData.getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        return node;
    }

    public Peer(int peerId, TorrentFile torrentFile, byte[] initialFileContent) throws DrasylException {
        DrasylConfig peerConfig = DrasylConfig.newBuilder()
                .identityPath(Path.of("/Users/marcodaum/IdeaProjects/AnonymousTorrent/peer_" + peerId + ".identity"))
                .networkId(22527)
                .remoteEnabled(false)
                .build();

        DrasylNode node = createNode(peerId, peerConfig, torrentFile);

        node.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
