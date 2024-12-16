package org.Torrent;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.*;
import org.drasyl.node.event.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Torrent.files.TrackerFile;
import org.Torrent.files.TrackerFiles;


public class Tracker {

    private final DrasylConfig TrackerConfig;

    private final DrasylNode node;

    private final TrackerFiles trackerFiles;


    public Tracker(int id) throws DrasylException {
        this.TrackerConfig = DrasylConfig.newBuilder()
                .identityPath(Path.of("/Users/marcodaum/IdeaProjects/AnonymousTorrent/tracker_" + id + ".identity"))
                .networkId(22527)
                .remoteEnabled(false)
                .build();

        this.trackerFiles = new TrackerFiles();

        this.node = new DrasylNode(TrackerConfig) {
            @Override
            public void onEvent(final Event event) {
                if (event instanceof NodeUpEvent) {
                    System.out.println("Tracker " + id + ": on");
                }
                if (event instanceof MessageEvent) {
                    String payload = ((String)((MessageEvent) event).getPayload());
                    int payloadDivider = payload.indexOf(",");
                    String messageReason = payload.substring(0, payloadDivider);
                    String messageContent = payload.substring(payloadDivider + 1);
                    DrasylAddress senderAddress = ((MessageEvent) event).getSender();

                    switch (messageReason) {
                        case "fileInfoRequest":
                            TrackerFile fileInfo = trackerFiles.getTrackerFiles().get(messageContent);
                            ObjectMapper objectMapper = new ObjectMapper();
                            String jsonString = null;
                            try {
                                jsonString = objectMapper.writeValueAsString(fileInfo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // Convention: First element is always the reason of the message, separated by a ',' to the actual payload
                            send(senderAddress, "fileInfo," + jsonString);
                            break;
                        case "filePublication":
                            if ((trackerFiles.getTrackerFiles().containsKey(messageContent))) {
                                if (!trackerFiles.getTrackerFile(messageContent).getSeederIds().contains(senderAddress.toString())) {
                                    trackerFiles.getTrackerFile(messageContent).addSeeder(senderAddress.toString());
                                    System.out.println("Tracker " + id + ": Publishing " + messageContent + " for " + senderAddress + " to existing File");
                                } else {
                                    System.out.println("Tracker " + id + ": Seeder Id " + senderAddress + " already published");
                                }
                            } else {
                                System.out.println("Tracker " + id + ": Publishing " + messageContent + " for " + senderAddress + " as new File");
                                trackerFiles.addTrackerFile(new TrackerFile(messageContent, node.identity().getAddress().toString(), new ArrayList<>(Arrays.asList(senderAddress.toString()))));
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        node.start();
    }
}
