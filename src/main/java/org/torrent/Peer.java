package org.torrent;

import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drasyl.node.event.NodeUpEvent;
import org.torrent.files.DistributedFile;
import org.torrent.files.TorrentFile;
import org.torrent.files.TrackerFile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class Peer {

    private DrasylNode createNode(int peerId, DrasylConfig peerConfig, TorrentFile torrentFile, int maxPeersPerFile, byte[] initialFileContent) throws DrasylException {
        // Convention: First element is always the reason of the message, separated by a ',' to the actual payload
        // Now that we have the tracker information, we can request the file at the peers
        // The message content is divided into 0) fileName 1) blockSize 2) offset (*blockSize)
        // The message content is divided into 0) fileName 1) blockSize 2) offset (*blockSize) 3) encoded file data
        return new DrasylNode(peerConfig) {

            private TrackerFile trackerFile = null;

            final HashMap<String, DistributedFile> fileData = new HashMap<>();

            public void askTrackerForFile() {
                // Convention: First element is always the reason of the message, separated by a ',' to the actual payload
                send(torrentFile.getTrackerId(), "fileInfoRequest," + torrentFile.getFilename());
            }

            public void requestFile() {
                if (trackerFile != null) {
                    ArrayList<String> seederIds = trackerFile.getSeederIds();
                    int peersPerFile = maxPeersPerFile;
                    if (seederIds.size() < maxPeersPerFile) {
                        peersPerFile = seederIds.size();
                    }
                    int sizePerPeer = 0;

                    // Check if we need a padding
                    if(torrentFile.getSizeInBytes() % peersPerFile != 0) {
                        sizePerPeer = (torrentFile.getSizeInBytes() / peersPerFile) + 1;
                    } else {
                        sizePerPeer = torrentFile.getSizeInBytes() / peersPerFile;
                    }

                    fileData.put(trackerFile.getFilename(), new DistributedFile(sizePerPeer * peersPerFile, torrentFile.getFilename()));

                    for (int i = 1; i <= peersPerFile; i++) {
                        System.out.println("Peer " + peerId + ": Sending request to peer..." + seederIds.get(i - 1));
                        send(seederIds.get(i - 1), "fileRequest," + torrentFile.getFilename() + "," + sizePerPeer + "," + (i - 1));
                    }
                } else {
                    System.out.println("Peer " + peerId + ": Could not download file. File not found!");
                }
            }

            public void publishFile() {
                send(torrentFile.getTrackerId(), "filePublication," + torrentFile.getFilename());
            }

            @Override
            public void onEvent(final Event event) {
                if (event instanceof NodeUpEvent) {
                    System.out.println("Peer " + peerId + ": on.");
                    if (initialFileContent != null) {
                        fileData.put(torrentFile.getFilename(), new DistributedFile(initialFileContent, torrentFile.getSizeInBytes(), torrentFile.getFilename()));
                        publishFile();
                    } else {
                        System.out.println("Peer: " + peerId + " Requesting file information from tracker...");
                        askTrackerForFile();
                    }
                }
                if (event instanceof MessageEvent) {
                    String payload = ((String)((MessageEvent) event).getPayload());
                    int payloadDivider = payload.indexOf(",");
                    String messageReason = payload.substring(0, payloadDivider);
                    String messageContent = payload.substring(payloadDivider + 1);
                    DrasylAddress senderAddress = ((MessageEvent) event).getSender();
                    switch (messageReason) {
                        case "fileInfo":
                            if (!messageContent.equals("null")) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    JSONParser parser = new JSONParser();
                                    trackerFile = new TrackerFile((JSONObject) parser.parse(messageContent));
                                    // Now that we have the tracker information, we can request the file at the peers
                                    requestFile();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("Peer " + peerId + ": Tracker does not know file. Response: " + messageContent);
                            }
                            break;
                        case "fileRequest":
                            // The message content is divided into 0) fileName 1) blockSize 2) offset (*blockSize)
                            String[] request_payloadElements = messageContent.split(",");
                            DistributedFile requestedFile = fileData.get(request_payloadElements[0]);
                            int request_blockSize =  Integer.parseInt(request_payloadElements[1]);
                            int request_offset =  Integer.parseInt(request_payloadElements[2]);
                            byte[] request_dataToSend = requestedFile.getDataChunk(request_offset * request_blockSize, request_blockSize);
                            send(senderAddress, "fileIncome," + request_payloadElements[0] + "," + request_payloadElements[1] + "," + request_payloadElements[2] + "," + Base64.getEncoder().encodeToString(request_dataToSend));
                            System.out.println("Peer " + peerId + ": file Request received!");
                        break;
                        case "fileIncome":
                            // The message content is divided into 0) fileName 1) blockSize 2) offset (*blockSize) 3) encoded file data
                            String[] income_payloadElements = messageContent.split(",");
                            if (income_payloadElements[0].equals(torrentFile.getFilename())) {
                                DistributedFile incomingFile = fileData.get(torrentFile.getFilename());
                                int incoming_blockSize =  Integer.parseInt(income_payloadElements[1]);
                                int incoming_offset =  Integer.parseInt(income_payloadElements[2]);
                                byte[] decodedData = Base64.getDecoder().decode(income_payloadElements[3]);
                                incomingFile.addDataChunk(decodedData, incoming_offset * incoming_blockSize, incoming_blockSize);
                                System.out.println("Peer " + peerId + ": Incoming file from " + senderAddress);
                            } else {
                                System.out.println("Peer " + peerId + ": Incoming file does not match the file, specified in the torrent file!");
                            }

                        default:
                            break;
                    }
                }
            }
        };
    }

    public Peer(int peerId, TorrentFile torrentFile, int maxPeersPerFile, byte[] initialFileContent) throws DrasylException {
        DrasylConfig peerConfig = DrasylConfig.newBuilder()
                .identityPath(Path.of("/Users/marcodaum/IdeaProjects/AnonymousTorrent/peer_" + peerId + ".identity"))
                .networkId(22527)
                .remoteEnabled(false)
                .build();

        DrasylNode node = createNode(peerId, peerConfig, torrentFile, maxPeersPerFile, initialFileContent);

        node.start();
    }
}
