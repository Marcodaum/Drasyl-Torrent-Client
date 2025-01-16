package org.torrent.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.BitSet;

public class DistributedFile {
    private boolean isComplete = false;
    private int fileSize;
    private String fileName;
    private BitSet downloadedChunks = null;
    private int noOfDownloadedChunks = 0;
    private RandomAccessFile file;
    private String assignedPeerId = "";

    // Already existing file
    public DistributedFile(File initialFile, int fileSize, String fileName, String peerId) {
        this.fileSize = fileSize;
        try {
            this.file = new RandomAccessFile(initialFile, "r");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.fileName = fileName;
        this.isComplete = true;
        this.assignedPeerId = peerId;
    }

    // Empty file
    public DistributedFile(int fileSize, String fileName, String peerId) {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.assignedPeerId = peerId;
    }

    public int addDataChunk(byte[] chunkData, int offset, int length) {
        int padding = calculatePadding(length);

        if ((this.fileSize + padding) % length != 0) {
            System.out.println("File " + this.getFileName()
                    + ": Cannot initialize Data-Chunks, as the chunk length is not a divisor of the file size! "
                    + getFileSize());
            return -1;
        }

        int index = offset / length;

        // First time to write to the file: Initialize chunks BitSet
        if (this.downloadedChunks == null) {
            this.noOfDownloadedChunks = (this.fileSize + padding) / length;
            this.downloadedChunks = new BitSet(this.noOfDownloadedChunks);
        }

        int chunkSize = (this.fileSize + padding) / this.noOfDownloadedChunks;

        if (index >= this.noOfDownloadedChunks || length > chunkSize) {
            throw new IllegalArgumentException("Invalid chunk index or data size");
        }

        try {
            file = new RandomAccessFile("buffer_files" + File.separator
                    + this.assignedPeerId + this.fileName, "rw");
            file.setLength(this.fileSize);
            file.seek(offset);

            if (isLastChunk(offset, length)) {
                file.write(Arrays.copyOf(chunkData, chunkData.length - padding));
            } else {
                file.write(chunkData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing data chunk to file", e);
        }

        downloadedChunks.set(index);

        return getCompletionPercentage();
    }

    public byte[] getDataChunk(int offset, int length) {
        if (!isComplete()) {
            System.out.println("Peer: " + this.assignedPeerId + " File not ready!");
            return null;
        }

        byte[] chunkToReturn;
        int padding = calculatePadding(length);

        if (isLastChunk(offset, length) && padding != 0) {
            chunkToReturn = new byte[length - padding];
        } else {
            chunkToReturn = new byte[length];
        }

        try {
            file.seek(offset);
            file.readFully(chunkToReturn);
        } catch (IOException e) {
            throw new RuntimeException("Error reading data chunk from file", e);
        }

        return chunkToReturn;
    }


    /*
    Helper Functions
     */

    private int calculatePadding(int chunkLength) {
        int chunksPerFile = (this.fileSize + chunkLength - 1) / chunkLength;
        return chunksPerFile * chunkLength - this.fileSize;
    }

    private boolean isLastChunk(int offset, int length) {
            return  (offset + length) >= this.fileSize;
        }

    private boolean isComplete() {
        if (this.isComplete) {
            return true;
        } else if (this.downloadedChunks == null) {
            return false;
        }
        return this.downloadedChunks.cardinality() == this.noOfDownloadedChunks;
    }

    private int getCompletionPercentage() {
        int finishedChunks = this.downloadedChunks.cardinality();
        return (int) (((double) finishedChunks / this.noOfDownloadedChunks) * 100);
    }


    /*
    Setters and Getters
     */

    public void setFileName(String fileName) {this.fileName = fileName;}

    public String getFileName() {return fileName;}

    public void setFileSize(int fileSize) {this.fileSize = fileSize;}

    public int getFileSize() {return this.fileSize;}

    public void setFile(RandomAccessFile file) {this.file = file;}

    public RandomAccessFile getFile() {return this.file;}

}
