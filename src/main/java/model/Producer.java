package model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import service.ProducerService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@RequiredArgsConstructor
public class Producer implements Runnable {
    @NonNull
    private BlockingQueue<JsonNode> blockingQJsonNode;
    @NonNull
    private JsonNode flagJsonNode;
    @NonNull
    private int numberOfProd;
    @NonNull
    private String url;
    @NonNull
    private int prodPeriod;
    @NonNull
    private int prodLoop;
    @NonNull
    private AtomicInteger threadXLoop;
    private ProducerService producerService = new ProducerService();

    @Override
    public void run() {
        producerService.takeBigJsonNodeAndPutRawOffersToBlockingQ(url, prodLoop, threadXLoop, blockingQJsonNode,
                prodPeriod, numberOfProd, flagJsonNode);
    }
}
