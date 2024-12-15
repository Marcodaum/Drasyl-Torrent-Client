package org.example.files;

import org.json.simple.JSONObject;

public class TorrentFile {
    public TorrentFile(JSONObject torrentFile) {
        this.id = ((Long) torrentFile.get("id")).intValue();
        this.filename = (String) torrentFile.get("filename");
        this.trackerId = (String) torrentFile.get("trackerId");
    }

    private int id;
    private String filename;
    private String trackerId;


    public int getId() {return id;}
    public String getTrackerId() {return trackerId;}
    public String getFilename() {return filename;}

    public void setId(int id) {this.id = id;}
    public void setTrackerId(String trackerId) {this.trackerId = trackerId;}
    public void setFilename(String filename) {this.filename = filename;}
}
