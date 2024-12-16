package org.torrent.files;

import org.json.simple.JSONObject;

public class TorrentFile {
    public TorrentFile(JSONObject torrentFile) {
        this.filename = (String) torrentFile.get("filename");
        this.trackerId = (String) torrentFile.get("trackerId");
        this.sizeInBytes = ((Long) torrentFile.get("sizeInBytes")).intValue();
    }

    private String filename;
    private String trackerId;
    private int sizeInBytes;


    public String getTrackerId() {return trackerId;}
    public String getFilename() {return filename;}
    public int getSizeInBytes() {return sizeInBytes;}

    public void setTrackerId(String trackerId) {this.trackerId = trackerId;}
    public void setFilename(String filename) {this.filename = filename;}
    public void setSizeInBytes(int sizeInBytes) {this.sizeInBytes = sizeInBytes;}
}
