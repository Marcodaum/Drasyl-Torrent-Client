package org.example;

import org.drasyl.node.*;
import org.example.files.TorrentFile;
import org.example.files.TorrentFiles;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.*;


public class Main {
    public static void main(final String[] args) throws DrasylException, InterruptedException {
        HashMap<String, byte[]> files = loadFiles();

        // Create Tracker
        Tracker currentTracker = new Tracker(1);

        // Import all available TorrentFiles from JSON file
        TorrentFiles torrentFiles = new TorrentFiles();

        // Create Peers and specify the file, the peer wants to download
        Peer currentPeer = new Peer(1, torrentFiles.getTorrentFile("testdatei"), files.get("testdatei"));
        Peer currentPeer2 = new Peer(2, torrentFiles.getTorrentFile("testfile3"), null);
    }

    public static HashMap<String, byte[]> loadFiles() {
        org.json.simple.parser.JSONParser parser = new JSONParser();
        JSONObject parsedJsonObject = null;
        JSONArray parsedJsonArray = null;
        HashMap<String, byte[]> files  = new HashMap<>();
        try {
            parsedJsonObject = (JSONObject) parser.parse(new FileReader("/Users/marcodaum/IdeaProjects/AnonymousTorrent/files.json"));
            parsedJsonArray = (JSONArray) parsedJsonObject.get("file-paths");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        for (Object filePath : parsedJsonArray)
        {
            File currentFile = new File((String) filePath);
            System.out.println((String)filePath);
            try {
                files.put(currentFile.getName() ,Files.readAllBytes(currentFile.toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return files;
    }
}

