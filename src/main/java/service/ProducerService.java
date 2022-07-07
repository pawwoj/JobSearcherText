package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.JsonNodeFromUrlException;
import exceptions.JsonNodeToBlockingQueueException;
import exceptions.ThreadSleepException;
import model.ObjectMapperHolder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerService {

    private ObjectMapper objectMapper = ObjectMapperHolder.INSTANCE.getMapper();

    public URL getUrlFromString(String string) {
        URL url;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            try {
                url = Path.of(string).toUri().toURL();
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return url;
    }

    public JsonNode getSourceJsonFromURL(URL url) {
        JsonNode json = null;
        try {
            json = objectMapper.readTree(url);
        } catch (IOException e) {
            throw new JsonNodeFromUrlException(e.getMessage());
        }
        return json;
    }

    public boolean putToBlockingQRawOffersFromBigNode(JsonNode sourceJsonNode,
                                                      int prodLoop,
                                                      AtomicInteger threadXLoop,
                                                      BlockingQueue<JsonNode> blockingQJsonNode,
                                                      int prodPeriod) {
        for (int i = 0; i < prodLoop; i++) {
            int x = threadXLoop.getAndIncrement();

            try {
                if (sourceJsonNode.size() <= x) {
                    System.out.println("P1 end x = " + x + " - size = "+ sourceJsonNode.size() + " " +
                            Thread.currentThread().getName() + " " + sourceJsonNode.get(x));
                    return true;
                }
                System.out.println("P1 put x = " + x + " " +
                        Thread.currentThread().getName() + " " + sourceJsonNode.get(x));
                blockingQJsonNode.put(sourceJsonNode.get(x));
            } catch (Exception e) {
                throw new JsonNodeToBlockingQueueException(e.getMessage());
            }
            try {
                Thread.sleep(prodPeriod);
            } catch (InterruptedException e) {
                throw new ThreadSleepException(e.getMessage());
            }
        }
        return true;
    }

    public boolean putFlagToBlockingQAtEndOfLoop(JsonNode bigJsonNode,
                                                 AtomicInteger threadXLoop,
                                                 int numberOfProd,
                                                 int prodLoop,
                                                 BlockingQueue<JsonNode> blockingQJsonNode,
                                                 JsonNode flagJsonNode) {
        if (threadXLoop.get() == numberOfProd * prodLoop || bigJsonNode.size() < threadXLoop.get()) {
//            System.out.println("ATOMIC FLAG " + Thread.currentThread().getName() + " " + threadXLoop.get());
            try {
                blockingQJsonNode.put(flagJsonNode);
            } catch (InterruptedException e) {
                throw new JsonNodeToBlockingQueueException(e.getMessage());
            }
        }
        return true;
    }

    public boolean takeBigJsonNodeAndPutRawOffersToBlockingQ(String urlString,
                                                             int prodLoop,
                                                             AtomicInteger threadXLoop,
                                                             BlockingQueue<JsonNode> blockingQJsonNode,
                                                             int prodPeriod,
                                                             int numberOfProd,
                                                             JsonNode flagJsonNode) {
        URL url = getUrlFromString(urlString);
        JsonNode bigJsonNode = getSourceJsonFromURL(url);
//        System.out.println("Start P " + Thread.currentThread().getName());
        putToBlockingQRawOffersFromBigNode(bigJsonNode, prodLoop, threadXLoop, blockingQJsonNode, prodPeriod);
        putFlagToBlockingQAtEndOfLoop(bigJsonNode, threadXLoop, numberOfProd,
                prodLoop, blockingQJsonNode, flagJsonNode);
        return true;
    }
}
