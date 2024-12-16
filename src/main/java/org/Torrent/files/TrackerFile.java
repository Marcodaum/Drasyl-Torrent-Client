package org.Torrent.files;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class TrackerFile {
    private String filename;
    private String trackerId;
    private ArrayList<String> seederIds = new ArrayList<>();

    public TrackerFile(JSONObject torrentFile) {
        this.filename = (String) torrentFile.get("filename");
        this.trackerId = (String) torrentFile.get("trackerId");
        JSONArray seeders = (JSONArray) torrentFile.get("seederIds");
        for (Object seeder : seeders) {
            this.seederIds.add((String) seeder);
        }
    }

    public TrackerFile(String filename, String trackerId, ArrayList<String> seederIds) {
        this.filename = filename;
        this.trackerId = trackerId;
        this.seederIds = seederIds;
    }

    public String getFilename() {return this.filename;}
    public String getTrackerId() {return this.trackerId;}
    public ArrayList<String> getSeederIds() {return this.seederIds;}

    public void setFilename(String filename) {this.filename=filename;}
    public void setTrackerId(String trackerId) {this.trackerId=trackerId;}
    public void setSeederIds(ArrayList<String> seederIds) {this.seederIds=seederIds;}
    public void addSeeder(String seederId) {this.seederIds.add(seederId);}
}
