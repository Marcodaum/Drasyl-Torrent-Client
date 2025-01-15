package org.torrent.files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;

public class DistributedFile {
    private boolean isComplete = false;
    private int fileSize;
    private String fileName;
    private BitSet chunks = null;
    private int noOfChunks = 0;
    private RandomAccessFile file;
    private int assignedPeerId = 0;


    public DistributedFile(RandomAccessFile initialFile, int fileSize, String fileName, int peerId) {
        this.fileSize = fileSize;
        this.file = initialFile;
        this.fileName = fileName;
        this.isComplete = true;
        this.assignedPeerId = peerId;
    }

    public DistributedFile(int fileSize, String fileName, int peerId) {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.assignedPeerId = peerId;
    }

    public boolean isComplete() {
        if (this.isComplete) {
            return true;
        } else if (this.chunks == null) {
            return false;
        }
        return this.chunks.cardinality() == this.noOfChunks;
    }

    public int getCompletionPercentage() {
        int finishedChunks = 0;
        for (int i = 0; i < this.noOfChunks; i++) {
            if (this.chunks.get(i)) {
                finishedChunks++;
            }
        }
        return (int)(((double)finishedChunks / this.noOfChunks) * 100);
    }

    public byte[] getAllData() throws IOException {
        byte[] data = new byte[(int) this.fileSize];
        file.seek(0);
        file.readFully(data);
        return data;
    }

    public byte[] getDataChunk(int offset, int length) {
        if (isComplete()) {
            if (offset < 0 || offset + length > this.fileSize) {
                throw new IllegalArgumentException("Invalid offset or length");
            }
            byte[] chunkToReturn = new byte[length];
            try {
                file.seek(offset);
                file.readFully(chunkToReturn);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return chunkToReturn;
        } else {
            System.out.println("File not ready!");
            return null;
        }
    }

    public boolean addDataChunk(byte[] chunkData, int offset, int length) {
        if (this.fileSize % length == 0) {
            int index = offset / length;
            if (chunks == null) {
                // Calculate the number of chunks, necessary to complete the file. All chunks have the same size
                noOfChunks = this.fileSize / length;
                chunks = new BitSet(noOfChunks);
                int chunkSize = this.fileSize / noOfChunks;

                try {
                    file = new RandomAccessFile("/Users/marcodaum/IdeaProjects/AnonymousTorrent/buffer_files/" + this.assignedPeerId + this.fileName, "rw");
                    file.setLength(this.fileSize);
                    if (index >= noOfChunks || length > chunkSize) {
                        throw new IllegalArgumentException("Invalid chunk index or data size");
                    }
                    file.seek(offset);
                    file.write(chunkData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            chunks.set(index); // Mark this chunk as received
            /* if (this.isComplete()) {
                try {
                    file.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }*/
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
