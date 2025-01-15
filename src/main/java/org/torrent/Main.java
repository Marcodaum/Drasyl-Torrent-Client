package org.torrent;

import org.drasyl.node.*;
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
        HashMap<String, RandomAccessFile> files = loadFiles();

        // Create Tracker
        Tracker currentTracker = new Tracker(1);

        // Import all available TorrentFiles from JSON file
        TorrentFiles torrentFiles = new TorrentFiles();

        // Create Peers and specify the file, the peer wants to download
        Peer currentPeer1 = new Peer(1, torrentFiles.getTorrentFile("testdatei1"), 100, files.get("testdatei1"));
        wait1s();
        Peer currentPeer2 = new Peer(2, torrentFiles.getTorrentFile("testdatei1"), 100, files.get("testdatei1"));
        wait1s();
        Peer currentPeer3 = new Peer(3, torrentFiles.getTorrentFile("testdatei1"), 100, files.get("testdatei1"));
        wait1s();
        Peer currentPeer4 = new Peer(4, torrentFiles.getTorrentFile("testdatei2"), 100, files.get("testdatei2"));
        wait1s();
        Peer currentPeer5 = new Peer(5, torrentFiles.getTorrentFile("testdatei2"), 100, files.get("testdatei2"));
        wait1s();
        Peer currentPeer6 = new Peer(6, torrentFiles.getTorrentFile("testdatei1"), 100, files.get("testdatei1"));
        wait1s();
        Peer currentPeer7 = new Peer(7, torrentFiles.getTorrentFile("testdatei1"), 100, files.get("testdatei1"));


        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Peer currentPeer8 = new Peer(8, torrentFiles.getTorrentFile("testdatei1"), 100, null);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Newly created peer now also uses peer 9 as it downloaded the complete "testdatei1"
        Peer currentPeer9 = new Peer(9, torrentFiles.getTorrentFile("testdatei1"), 100, null);

        Peer currentPeer10 = new Peer(10, torrentFiles.getTorrentFile("testdatei2"), 100, null);


        while(true) {}
    }

    public static void wait1s() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, RandomAccessFile> loadFiles() {
        org.json.simple.parser.JSONParser parser = new JSONParser();
        JSONObject parsedJsonObject = null;
        JSONArray parsedJsonArray = null;
        HashMap<String, RandomAccessFile> files  = new HashMap<>();
        try {
            parsedJsonObject = (JSONObject) parser.parse(new FileReader("/Users/marcodaum/IdeaProjects/AnonymousTorrent/files.json"));
            parsedJsonArray = (JSONArray) parsedJsonObject.get("file-paths");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        for (Object filePath : parsedJsonArray)
        {
            File currentFile = new File((String) filePath);
            RandomAccessFile actualFile = null;
            try {
                actualFile = new RandomAccessFile(currentFile, "r");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            files.put(currentFile.getName(), actualFile);
        }
        return files;
    }
}

