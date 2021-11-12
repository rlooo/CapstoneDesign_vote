package org.hyperledger.fabric.chaincode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.chaincode.models.Vote;
import org.hyperledger.fabric.chaincode.models.VoteKey;
import org.hyperledger.fabric.protos.peer.ChaincodeSupportGrpc;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static  java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.shim.ResponseUtils.newErrorResponse;
import static org.hyperledger.fabric.shim.ResponseUtils.newSuccessResponse;

public class chaincode extends ChaincodeBase{
    private static Log _logger=LogFactory.getLog(chaincode.class);

    @Override
    public Response init(ChaincodeStub stub){
        try{
            List<String> args = stub.getStringArgs();
            if(args.size() != 2){
                newErrorResponse("Error Incorrect arguments.");
            }
            stub.putStringState(args.get(0), args.get(1));
            return newSuccessResponse();
        } catch (Throwable e){
            return newErrorResponse("Failed to create asset");
        }
    }
    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String func = stub.getFunction();
            List<String> params = stub.getParameters();
            if (func.equals("vote")) {
                return vote(stub, params);
            }else if (func.equals("setVote")){
                return setVote(stub, params);
            }else if (func.equals("getAllVote")){
                return getAllVote(stub);
            }
            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"vote\", \"setVote\", \"getAllVote\"");
        } catch (Throwable e) {
            return newErrorResponse(e.getMessage());
        }
    }

    private VoteKey generateKey(ChaincodeStub stub){
        boolean isFirst = false;

        String jsonValue = stub.getStringState("latestKey");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        VoteKey votekey = gson.fromJson(jsonValue, VoteKey.class);
        String tempIdx = String.valueOf(votekey.getIdx());
        _logger.info(String.format("Key is %s", tempIdx));

        if(votekey.getKey().length()==0 || votekey.getKey().equals("")){
            isFirst = true;
            votekey.setKey("VT");
        }

        if(!isFirst){
            votekey.setIdx(votekey.getIdx()+1);
        }

        _logger.info(String.format("Last key is %s : %s", votekey.getKey(), tempIdx));

        return votekey;
    }

    private Response setVote(ChaincodeStub stub, List<String> args) throws ParseException {
        if(args.size() != 5){
            throw new RuntimeException("Error Incorrect arguments.");
        }

        VoteKey votekey = generateKey(stub);
        String keyIdx = String.valueOf(votekey.getIdx());
        _logger.info(String.format("Key is %s", keyIdx));

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] itemList = new String[3];
        itemList[0]=args.get(2);
        itemList[1]=args.get(3);
        itemList[2]=args.get(4);

        Vote vote = new
                Vote(args.get(0), transFormat.parse(args.get(1)), itemList);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String keyString = votekey.getKey() + keyIdx;
        try{
            stub.putState(keyString, gson.toJson(vote).getBytes());
            stub.putState("latestKey", gson.toJson(votekey).getBytes());
            return newSuccessResponse("Vote created");
        } catch(Throwable e){
            return newErrorResponse(e.getMessage());
        }
    }

    private Response getAllVote(ChaincodeStub stub){
        String jsonValue = stub.getStringState("latestKey");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        VoteKey votekey = gson.fromJson(jsonValue, VoteKey.class);
        String idxStr = String.valueOf(votekey.getIdx()+1);

        String startKey = "VT0";
        String endKey = votekey.getKey() + idxStr;

        String result = "[";
        QueryResultsIterator<KeyValue> rows = stub.getStateByRange(startKey, endKey);
        while(rows.iterator().hasNext()){
            String v = rows.iterator().next().getStringValue();
            if(v != null && v.trim().length() >0){
                result = result.concat(v);
                if(rows.iterator().hasNext())
                    result = result.concat(",");
            }
        }
        result = result.concat("]");

        return newSuccessResponse(result, ByteString.copyFrom(result, UTF_8).toByteArray());
    }

    private Response vote(ChaincodeStub stub, List<String> args){
        String voteKey = args.get(0);

        String jsonValue = stub.getStringState(voteKey);
        if(jsonValue == null){
            return newErrorResponse(String.format("Entity %s not found", voteKey));
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Vote vote = gson.fromJson(jsonValue, Vote.class);

        int[] count = vote.getCount();
        int no = Integer.parseInt(args.get(1));

        vote.getCount()[no]++;
        vote.setCount(count);

        return newSuccessResponse("invoke finished successfully");
    }
    public static void main(String[] args){
        new chaincode().start(args);
    }
}
