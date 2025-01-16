package org.torrent;

import org.drasyl.node.*;
import org.torrent.files.PeerPool;
import org.torrent.files.TorrentFiles;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(final String[] args) throws DrasylException {
        HashMap<String, File> files = loadFiles();

        // Create Tracker
        Tracker currentTracker = new Tracker(1);

        // Import all available TorrentFiles from JSON file
        TorrentFiles torrentFiles = new TorrentFiles();

        // Create Peers and specify the file, the peer wants to download
        // Input Number of peers. Here: Hard-Coded. However, should be a CLI argument
        int noOfPeers = Integer.parseInt("10");

        Peer[] peers = new Peer[noOfPeers];

        PeerPool seeder_liedmp3s = new PeerPool(8, torrentFiles.getTorrentFile("lied.mp3"), 0, files.get("lied.mp3"));
        wait1s();
        PeerPool downloader_liedmp3 = new PeerPool(2, torrentFiles.getTorrentFile("lied.mp3"), 0);

        while(true) {}
    }

    public static void wait1s() {
        try {
            TimeUnit.MILLISECONDS.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, File> loadFiles() {
        org.json.simple.parser.JSONParser parser = new JSONParser();
        JSONObject parsedJsonObject = null;
        JSONArray parsedJsonArray = null;
        HashMap<String, File> files  = new HashMap<>();
        try {
            parsedJsonObject = (JSONObject) parser.parse(new FileReader("config_files" + File.separator + "files.json"));
            parsedJsonArray = (JSONArray) parsedJsonObject.get("file-paths");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        for (Object filePath : parsedJsonArray)
        {
            File currentFile = new File((String) filePath);
            files.put(currentFile.getName(), currentFile);
        }
        return files;
    }
}

