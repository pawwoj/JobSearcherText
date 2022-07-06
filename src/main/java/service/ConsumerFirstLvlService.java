package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.JobOfferFromJsonNodeException;
import exceptions.JobOfferToBlockingQueueException;
import exceptions.JsonNodeToBlockingQueueException;
import exceptions.JsonNodeFromBlockingQueueException;
import model.JobOffer;
import model.ObjectMapperHolder;

import java.util.concurrent.BlockingQueue;

public class ConsumerFirstLvlService {

    private ObjectMapper objectMapper = ObjectMapperHolder.INSTANCE.getMapper();

    public boolean putFlagToJsonNodeBlockingQ(BlockingQueue<JsonNode> blockingQJsonNode,
                                              JsonNode flagJsonNode) {
        try {
            blockingQJsonNode.put(flagJsonNode);
            return true;
        } catch (InterruptedException e) {
            throw new JsonNodeToBlockingQueueException(e.getMessage());
        }
    }

    public boolean putFlagToJobOfferBlockingQ(BlockingQueue<JobOffer> blockingQJobOffer,
                                              JobOffer flagJobOffer) {
        try {
            blockingQJobOffer.put(flagJobOffer);
            return true;
        } catch (InterruptedException e) {
            throw new JobOfferToBlockingQueueException(e.getMessage());
        }
    }

    public JobOffer buildJobOfferFromJsonNode(JsonNode jsonNode) {
        JobOffer jobOffer = null;
        try {
            jobOffer = objectMapper.treeToValue(jsonNode, JobOffer.class);
        } catch (Exception e) {
            throw new JobOfferFromJsonNodeException(e.getMessage());
        }
        return jobOffer;
    }

    public boolean isNodeFlag(JsonNode currentJsonNode,
                              JsonNode flagJsonNode) {
        boolean isFlag = false;
        if (currentJsonNode.equals(flagJsonNode)) {
            isFlag = true;
//            System.out.println("end C1 " + Thread.currentThread().getName());
        }
        return isFlag;
    }

    public JsonNode takeJsonNodeFromBlockingQ(BlockingQueue<JsonNode> blockingQJsonNode) {
        JsonNode jsonNode = null;
        try {
            jsonNode = blockingQJsonNode.take();
        } catch (InterruptedException e) {
            throw new JsonNodeFromBlockingQueueException(e.getMessage());
        }
        return jsonNode;
    }

    public boolean putJobOfferToBlockingQ(BlockingQueue<JobOffer> blockingQJobOffer, JobOffer jobOffer) {
        try {
            blockingQJobOffer.put(jobOffer);
            return true;
        } catch (Exception e) {
            throw new JobOfferToBlockingQueueException(e.getMessage());
        }
    }

    public boolean takeJsonNodeAndPutJobOffer(BlockingQueue<JsonNode> blockingQJsonNode,
                                           BlockingQueue<JobOffer> blockingQJobOffer,
                                           JsonNode flagJsonNode,
                                           JobOffer flagJobOffer) {
        while (true) {
//            if (blockingQJsonNode.contains(flagJsonNode)){
//                putFlagToJobOfferBlockingQ(blockingQJobOffer, flagJobOffer);
//                return;
//            }
            JsonNode jsonNode = takeJsonNodeFromBlockingQ(blockingQJsonNode);
            if (isNodeFlag(jsonNode, flagJsonNode)) {
                putFlagToJobOfferBlockingQ(blockingQJobOffer, flagJobOffer);
                putFlagToJsonNodeBlockingQ(blockingQJsonNode, flagJsonNode);
                return true;
            }
            JobOffer jobOffer = buildJobOfferFromJsonNode(jsonNode);
            putJobOfferToBlockingQ(blockingQJobOffer, jobOffer);
        }
    }
}
