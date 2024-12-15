package org.example.files;

public class TrackerFile {
    private int id;
    private String filename;
    private int trackerId;
    private String[] seederIds;

    public int getId() {return this.id;}
    public String getFilename() {return this.filename;}
    public int getTrackerId() {return this.trackerId;}
    public String[] getSeederIds() {return this.seederIds;}

    public void setId(int id) {this.id=id;}
    public void setFilename(String filename) {this.filename=filename;}
    public void setTrackerId(int trackerId) {this.trackerId=trackerId;}
    public void setSeederIds(String[] seederIds) {this.seederIds=seederIds;}
}
