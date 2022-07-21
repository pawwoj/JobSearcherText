package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.JsonNodeFromUrlException;
import exceptions.JsonNodeToBlockingQueueException;
import model.ObjectMapperHolder;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class ProducerServiceTest {

    private ProducerService producerService;

    @Before
    public void init() {
        producerService = new ProducerService();
    }

    @Test
    public void shouldGetUrlFromStringHttp() {
        String s = "https://test.pl/test";
        assertThat(producerService.getUrlFromString(s)).isInstanceOf(URL.class);
    }

    @Test
    public void shouldGetUrlFromStringPath() {
        String s = "src/test/resources/bigJsonTest.json";
        assertThat(producerService.getUrlFromString(s)).isInstanceOf(URL.class);
    }

    @Test(expected = JsonNodeFromUrlException.class)
    public void shouldThrowJsonNodeFromUrlExceptionWhenUrlIsInvalid() {
        URL invalid;
        try {
            invalid = new URL("https://test.pl/test");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        producerService.getSourceJsonFromURL(invalid);
    }

    @Test
    public void shouldReturnJsonNodeAsExpected() {
        String path = "src/test/resources/bigJsonTest.json";
        URL url = null;
        try {
            url = Path.of(path).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JsonNode received = producerService.getSourceJsonFromURL(url);
        assertEquals(received.size(), 6);
    }

    @Test
    public void shouldAddNodesToBlockingQAndFinishOnExpectedLoop() {
        String path = "src/test/resources/bigJsonTest.json";
        URL url = null;
        try {
            url = Path.of(path).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JsonNode bigNode = producerService.getSourceJsonFromURL(url);
        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        int prodLoop = 3;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        int period = 10;
        boolean received = producerService
                .putToBlockingQRawOffersFromSourceNode(bigNode, prodLoop, atomicInteger, bQ, period);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isEqualTo(true);
        sAssert.assertThat(bQ.size()).isEqualTo(3);
        sAssert.assertThat(atomicInteger.get()).isEqualTo(3);
        sAssert.assertAll();
    }

    @Test
    public void shouldAddNodesToBlockingQAndFinishOnBigNodeEnd() {
        String path = "src/test/resources/bigJsonTest.json";
        URL url = null;
        try {
            url = Path.of(path).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JsonNode bigNode = producerService.getSourceJsonFromURL(url);
        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        int prodLoop = 7;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        int period = 10;

        boolean received = producerService
                .putToBlockingQRawOffersFromSourceNode(bigNode, prodLoop, atomicInteger, bQ, period);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isEqualTo(true);
        sAssert.assertThat(bQ.size()).isEqualTo(6);
        sAssert.assertThat(atomicInteger.get()).isEqualTo(7);
        sAssert.assertAll();
    }

    @Test(expected = JsonNodeToBlockingQueueException.class)
    public void shouldThrowJsonNodeToBlockingQueueExceptionWhenJsonNodeIsInvalid() {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = "{\"a\":\"test\",\"b\":\"test\",\"c\":12}";
        JsonNode bigNode = null;
        try {
            bigNode = objectMapper.readTree(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        int prodLoop = 5;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        int period = 10;
        producerService
                .putToBlockingQRawOffersFromSourceNode(bigNode, prodLoop, atomicInteger, bQ, period);

    }

    @Test
    public void shouldPutFlagAtEndOfLoop() {
        String path = "src/test/resources/bigJsonTest.json";
        URL url = null;
        try {
            url = Path.of(path).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JsonNode flag;
        try {
            flag = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode bigNode = producerService.getSourceJsonFromURL(url);
        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        int prodLoop = 2;
        AtomicInteger atomicInteger = new AtomicInteger(4);
        int numberOfProd = 2;

        boolean received = producerService
                .putFlagToBlockingQAtEndOfLoop(bigNode, atomicInteger, numberOfProd, prodLoop, bQ, flag);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isEqualTo(true);
        sAssert.assertThat(bQ.size()).isEqualTo(1);
        sAssert.assertThat(bQ.contains(flag)).isEqualTo(true);
        sAssert.assertAll();
    }

    @Test
    public void shouldPutFlagWhenGetMaxSizeOfSourceJsonNode() {
        String path = "src/test/resources/bigJsonTest.json";
        URL url = null;
        try {
            url = Path.of(path).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JsonNode flag;
        try {
            flag = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode bigNode = producerService.getSourceJsonFromURL(url);
        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        int prodLoop = 5;
        AtomicInteger atomicInteger = new AtomicInteger(7);
        int numberOfProd = 2;

        boolean received = producerService
                .putFlagToBlockingQAtEndOfLoop(bigNode, atomicInteger, numberOfProd, prodLoop, bQ, flag);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isEqualTo(true);
        sAssert.assertThat(bQ.size()).isEqualTo(1);
        sAssert.assertThat(bQ.contains(flag)).isEqualTo(true);
        sAssert.assertAll();
    }

    @Test
    public void shouldPutNodesWithRawOffersAndFlagToBlockingQueueWhenLoopEnd() {
        String path = "src/test/resources/bigJsonTest.json";
        JsonNode flag;
        try {
            flag = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        int prodLoop = 4;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        int numberOfProd = 1;
        int prodPeriod = 2;

        boolean received = producerService
                .takeBigJsonNodeAndPutRawOffersToBlockingQ(
                        path, prodLoop, atomicInteger, bQ, prodPeriod, numberOfProd, flag);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isTrue();
        sAssert.assertThat(bQ.size()).isEqualTo(5);
        sAssert.assertThat(bQ.contains(flag)).isTrue();
        sAssert.assertAll();
    }

    @Test
    public void shouldPutNodesWithRawOffersAndFlagToBlockingQueueWhenSourceNodeEnd() {
        String path = "src/test/resources/bigJsonTest.json";
        JsonNode flag;
        try {
            flag = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        int prodLoop = 8;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        int numberOfProd = 1;
        int prodPeriod = 2;

        boolean received = producerService
                .takeBigJsonNodeAndPutRawOffersToBlockingQ(
                        path, prodLoop, atomicInteger, bQ, prodPeriod, numberOfProd, flag);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isEqualTo(true);
        sAssert.assertThat(bQ.size()).isEqualTo(7);
        sAssert.assertThat(bQ.contains(flag)).isEqualTo(true);
        sAssert.assertAll();
    }
}