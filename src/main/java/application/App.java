package application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import model.*;
import service.DatabaseService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public class App {
    private BlockingQueue<JsonNode> blockingQueueRaw = new LinkedBlockingQueue<>(10);
    private BlockingQueue<JobOffer> blockingQueueOffer = new LinkedBlockingQueue<>(10);

    private JobOffer flagJobOffer = new JobOffer("!END-OF-LOOP!");
    private JsonNode flagJsonNode = getFlagJsonNode();
    private ExecutorService executorService;

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
                    String url,
                    DatabaseService databaseService) {

        AtomicInteger atomicPThreadXLoop = new AtomicInteger(0);
        executorService = Executors.newFixedThreadPool(numberOfProd + numberOfCons1 + numberOfCons2);

        Producer producer = new Producer(blockingQueueRaw,
                getFlagJsonNode(),
                numberOfProd,
                url,
                prodPeriod,
                prodLoop,
                atomicPThreadXLoop);
        for (int i = 0; i < numberOfProd; i++) {
            executorService.submit(producer);
        }

        ConsumerFirstLvl consumerFirstLvl = new ConsumerFirstLvl(blockingQueueRaw,
                blockingQueueOffer,
                getFlagJsonNode(),
                getFlagJobOffer());
        for (int i = 0; i < numberOfCons1; i++) {
            executorService.submit(consumerFirstLvl);
        }

        ConsumerSecondLvl consumerSecondLvl = new ConsumerSecondLvl(blockingQueueOffer, getFlagJobOffer(), databaseService);
        for (int i = 0; i < numberOfCons2; i++) {
            executorService.submit(consumerSecondLvl);
        }
        executorService.shutdown();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}