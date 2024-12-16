package org.Torrent.files;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TrackerFiles {
    private HashMap<String ,TrackerFile> trackerFiles = new HashMap<>();

    public TrackerFiles() {
        JSONParser parser = new JSONParser();
        JSONObject parsedJsonObject = null;
        JSONArray parsedJsonArray = null;
        try {
            parsedJsonObject = (JSONObject) parser.parse(new FileReader("/Users/marcodaum/IdeaProjects/AnonymousTorrent/tracker-files.json"));
            parsedJsonArray = (JSONArray) parsedJsonObject.get("files");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        for (Object file : parsedJsonArray)
        {
            JSONObject trackerFile = (JSONObject) file;
            this.trackerFiles.put((String) ((JSONObject) file).get("filename"), new TrackerFile(trackerFile));
        }
    }

    public HashMap<String, TrackerFile> getTrackerFiles() {return trackerFiles;}
    public void addTrackerFile(TrackerFile trackerFile) {this.trackerFiles.put(trackerFile.getFilename(), trackerFile);}
    public TrackerFile getTrackerFile(String filename) {return trackerFiles.get(filename);}
}
