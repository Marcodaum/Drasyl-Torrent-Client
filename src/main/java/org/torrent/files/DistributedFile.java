package org.torrent.files;

import java.util.Arrays;

public class DistributedFile {
    private boolean isComplete = false;
    private byte[] data;
    private int fileSize;
    private String fileName;
    private boolean[] chunks = null;
    private int chunkSize = 0;

    public DistributedFile(byte[] fullData, int fileSize, String fileName) {
        this.data = new byte[fileSize];
        this.fileSize = fileSize;
        this.data = fullData;
        this.fileName = fileName;
        this.isComplete = true;

    }

    public DistributedFile(int fileSize, String fileName) {
        this.data = new byte[fileSize];
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public boolean isComplete() {
        if (this.isComplete) {
            return true;
        }
        for(boolean b : this.chunks) if(!b) return false;
        this.isComplete = true;
        return true;
    }

    public int getCompletionPercentage() {
        int finishedChunks = 0;
        for (int i = 0; i < this.chunkSize; i++) {
            if (this.chunks[i]) {
                finishedChunks++;
            }
        }
        return (int)(((double)finishedChunks / this.chunkSize) * 100);
    }

    public byte[] getAllData() {return data;}


    public byte[] getDataChunk(int offset, int length) {
        if (isComplete()) {
            return Arrays.copyOfRange(data, offset, offset + length);
        } else {
            System.out.println("File not ready!");
            return null;
        }
    }
    public boolean addDataChunk(byte[] chunkData, int offset, int length) {
        if (this.fileSize % length == 0) {
            if (chunks == null) {
                // Calculate the number of chunks, necessary to complete the file. All chunks have the same size
                chunkSize = this.fileSize / length;
                chunks = new boolean[chunkSize];
            }
            System.arraycopy(chunkData, 0, this.data, offset, length);
            chunks[offset / length] = true;
        } else {
            System.out.println("File " + this.getFileName() + ": Can not initialize Data-Chunks, as the chunk length is no divider of the file size!" + getFileSize());
        }
        return this.isComplete();
    }

    public String getFileName() {return fileName;}

    public void setFileName(String fileName) {this.fileName = fileName;}

    /*private void getFillingStateOfFileChunks() {
        int readyChunks = 0;
        for (int i = 0; i < chunkSize; i++) {
            if (chunks[i]) {
                readyChunks += 1;
            }
        }
        int percentage = (int)(((double)readyChunks / chunkSize)) * 100;
        System.out.println("Now received " + percentage + "% of chunks.");
    }*/

    public void setFileSize(int fileSize) {this.fileSize = fileSize;}

    public int getFileSize() {return this.fileSize;}
}
