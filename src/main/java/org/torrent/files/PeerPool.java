package org.torrent.files;

import org.drasyl.node.DrasylException;
import org.torrent.Peer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PeerPool {
    int idCounter = 0;
    int noOfPeers = 0;
    TorrentFile torrentFile;
    Map<String, Peer> peers = new HashMap<String, Peer>();

    // Create a peer pool of peers that already know a file fully.
    public PeerPool(int noOfPeers, TorrentFile torrentFile, int maxChunksPerFile, File initialFile) throws DrasylException {
        this.noOfPeers = noOfPeers;
        this.torrentFile = torrentFile;
        for (int i = 0; i < noOfPeers; i++) {
            peers.put((i + this.idCounter) + "_" + torrentFile.getFilename() + "_seeder", new Peer((i + this.idCounter) + "_" + torrentFile.getFilename() + "_seeder", torrentFile, maxChunksPerFile, initialFile));
        }
        this.idCounter += noOfPeers;
    }

    public PeerPool(int noOfPeers, TorrentFile torrentFile, int maxChunksPerFile) throws DrasylException {
        this.noOfPeers = noOfPeers;
        this.torrentFile = torrentFile;
        for (int i = 0; i < noOfPeers; i++) {
            peers.put((i + this.idCounter) + "_" + torrentFile.getFilename(), new Peer((i + this.idCounter) + "_" + torrentFile.getFilename(), torrentFile, maxChunksPerFile, null));
        }
        this.idCounter += noOfPeers;
    }

    public void addPeer(Peer peer, int maxChunksPerFile) throws DrasylException {
        peers.put((this.idCounter) + "_" + torrentFile.getFilename(), new Peer((this.idCounter) + "_" + torrentFile.getFilename(), torrentFile, maxChunksPerFile, null));
        this.idCounter++;
    }

    public void addPeer(Peer peer, int maxChunksPerFile, File initialFile) throws DrasylException {
        peers.put((this.idCounter) + "_" + torrentFile.getFilename() + "_seeder", new Peer((this.idCounter) + "_" + torrentFile.getFilename() + "_seeder", torrentFile, maxChunksPerFile, initialFile));
        this.idCounter++;
    }
}
