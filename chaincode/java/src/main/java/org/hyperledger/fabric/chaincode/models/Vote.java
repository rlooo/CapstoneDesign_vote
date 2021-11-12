package org.hyperledger.fabric.chaincode.models;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.chaincode.chaincode;

import java.util.Date;
import java.util.Arrays;

public class Vote {
    private String voteName;
    private Date voteTime;
    private String item[];
    private int count[];

    public Vote(String voteName, Date voteTime, String[] item) {
        this.voteName = voteName;
        this.voteTime = voteTime;
        this.item = item;
        Arrays.fill(this.count,0);
    }

    private Vote() {
    }

    public String getVoteName() {
        return voteName;
    }

    public Date getVoteTime() {
        return voteTime;
    }

    public String[] getItem(){
        return item;
    }
    public int[] getCount(){
        return count;
    }

    public void setVoteName() {
        this.voteName = voteName;
    }

    public void setVoteTime() {
        this.voteTime = voteTime;
    }

    public void setItem(String[] item){
        this.item = item;
    }

    public void setCount(int[] count){
        this.count = count;
    }

    public int getTotal(){
        int sum = 0;
        for(int i=0;i<count.length;i++) sum+=count[i];
        return sum;
    }
}


