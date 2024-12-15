package org.example.files;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TorrentFiles {
    private HashMap<String, TorrentFile> torrentFiles = new HashMap<>();

    public TorrentFiles() {
        JSONParser parser = new JSONParser();
        JSONObject parsedJsonObject = null;
        JSONArray parsedJsonArray = null;
        try {
            parsedJsonObject = (JSONObject) parser.parse(new FileReader("/Users/marcodaum/IdeaProjects/AnonymousTorrent/torrent-files.json"));
            parsedJsonArray = (JSONArray) parsedJsonObject.get("torrents");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        for (Object file : parsedJsonArray)
        {
            JSONObject torrentFile = (JSONObject) file;
            this.torrentFiles.put((String) ((JSONObject) file).get("filename"), new TorrentFile(torrentFile));

        }
    }

    public TorrentFile getTorrentFile(String filename) {
        return this.torrentFiles.get(filename);
    }

    public void addTorrentFiles(TorrentFile torrentFile) {
        this.torrentFiles.put(torrentFile.getFilename(), torrentFile);
    }
}
