package org.example;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.node.*;
import org.drasyl.node.event.*;
import java.io.File;
import java.nio.file.Path;
import java.io.IOException;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.files.TrackerFile;
import org.example.files.TrackerFiles;


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

        this.trackerFiles = this.readTrackerFiles();

        this.node = new DrasylNode(TrackerConfig) {
            @Override
            public void onEvent(final Event event) {
                if (event instanceof NodeOnlineEvent) {
                    System.out.println("Tracker on");
                }
                if (event instanceof MessageEvent) {
                    String filename = (String)((MessageEvent) event).getPayload();
                    TrackerFile fileInfo = trackerFiles.getTrackerFiles().stream().filter(currentTrackerFile -> currentTrackerFile.getFilename().equals(filename)).findFirst().orElse(null);
                    DrasylAddress senderAddress = ((MessageEvent) event).getSender();
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = null;
                    try {
                        jsonString = objectMapper.writeValueAsString(fileInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Convention: First element is always the reason of the message, separated by a ',' to the actual payload
                    send(senderAddress, "fileInfo," + jsonString);
                }
            }
        };

        node.start();
    }

    private TrackerFiles readTrackerFiles() {
        TrackerFiles tracker = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            tracker = objectMapper.readValue(new File("/Users/marcodaum/IdeaProjects/AnonymousTorrent/tracker-files.json"), TrackerFiles.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tracker;
    }
}
