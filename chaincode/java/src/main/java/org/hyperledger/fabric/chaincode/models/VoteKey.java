package org.hyperledger.fabric.chaincode.models;

public class VoteKey {
    private String key;
    private int idx;


    public VoteKey(String key, int idx) {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }
}