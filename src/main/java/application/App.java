package application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import model.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    private BlockingQueue<JsonNode> blockingQueueRaw = new LinkedBlockingQueue<>(10);
    private BlockingQueue<JobOffer> blockingQueueOffer = new LinkedBlockingQueue<>(10);

    private JobOffer flagJobOffer = new JobOffer("!END-OF-LOOP!");
    private JsonNode flagJsonNode = getFlagJsonNode();

    public JobOffer getFlagJobOffer() {
        return flagJobOffer;
    }

    public JsonNode getFlagJsonNode() {
        JsonNode flag;
        try {
            flag = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }


    public void run(int numberOfProd,
                    int prodPeriod,
                    int prodLoop,
                    int numberOfCons1,
                    int numberOfCons2,
                    String url) {

        AtomicInteger atomicPThreadXLoop = new AtomicInteger(0);

        ExecutorService execProd = Executors.newFixedThreadPool(numberOfProd);
        ExecutorService execConsI = Executors.newFixedThreadPool(numberOfCons1);
        ExecutorService execConsII = Executors.newFixedThreadPool(numberOfCons2);

        Producer producer = new Producer(blockingQueueRaw,
                getFlagJsonNode(),
                numberOfProd,
                url,
                prodPeriod,
                prodLoop,
                atomicPThreadXLoop);
        for (int i = 0; i < numberOfProd; i++) {
            execProd.submit(producer);
        }

        ConsumerFirstLvl consumerFirstLvl = new ConsumerFirstLvl(blockingQueueRaw,
                blockingQueueOffer,
                getFlagJsonNode(),
                getFlagJobOffer());
        for (int i = 0; i < numberOfCons1; i++) {
            execConsI.submit(consumerFirstLvl);
        }

        ConsumerSecondLvl consumerSecondLvl = new ConsumerSecondLvl(blockingQueueOffer, getFlagJobOffer());
        for (int i = 0; i < numberOfCons2; i++) {
            execConsII.submit(consumerSecondLvl);
        }
        execProd.shutdown();
        execConsI.shutdown();
        execConsII.shutdown();
    }
}
