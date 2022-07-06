package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import exceptions.JobOfferFromJsonNodeException;
import exceptions.JobOfferToBlockingQueueException;
import model.JobOffer;
import model.ObjectMapperHolder;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerFirstLvlServiceTest {

    private ConsumerFirstLvlService service;

    @Before
    public void init() {
        service = new ConsumerFirstLvlService();
    }

    @Test
    public void shouldPutFlagToJNodeBlockingQ() {
        JsonNode flag;
        try {
            flag = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        BlockingQueue<JsonNode> bQ = new LinkedBlockingQueue<>(10);
        boolean received = service.putFlagToJsonNodeBlockingQ(bQ, flag);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(bQ.contains(flag)).isTrue();
        sAssert.assertThat(received).isTrue();
        sAssert.assertAll();
    }

    @Test
    public void shouldPutFlagToJobOfferBlockingQ() {
        JobOffer flag = new JobOffer("Flag");

        BlockingQueue<JobOffer> bQ = new LinkedBlockingQueue<>(10);
        boolean received = service.putFlagToJobOfferBlockingQ(bQ, flag);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(bQ.contains(flag)).isTrue();
        sAssert.assertThat(received).isTrue();
        sAssert.assertAll();
    }

    @Test
    public void shouldReturnExpectedJobOfferFromJsonNode() {
        JsonNode jsonNodeSource;
        String path = "src/test/resources/bigJsonTest.json";
        try {
            jsonNodeSource = ObjectMapperHolder.INSTANCE.getMapper().readTree(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JobOffer expected = new JobOffer("Offer1",
                "13300 - 15000", "id_test1",
                null,
                List.of("SQL", "C", "PHP"),
                "Java");

        JsonNode rawOffer = jsonNodeSource.get(0);
        JobOffer jobOffer = service.buildJobOfferFromJsonNode(rawOffer);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(jobOffer).isEqualTo(expected);
        sAssert.assertAll();
    }

    @Test(expected = JobOfferFromJsonNodeException.class)
    public void shouldThrowJobOfferFromJsonNodeExceptionWhenJsonNodeIsIncorrect() {
        JsonNode incorrectNode;
        try {
            incorrectNode = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        service.buildJobOfferFromJsonNode(incorrectNode);
    }

    @Test
    public void shouldPutJobOfferToBlockingQ() {
        JobOffer jobOffer = new JobOffer("Offer1",
                "13300 - 15000", "id_test1",
                null,
                List.of("SQL", "C", "PHP"),
                "Java");

        BlockingQueue<JobOffer> bQ = new LinkedBlockingQueue<>(10);
        boolean received = service.putJobOfferToBlockingQ(bQ, jobOffer);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(bQ.contains(jobOffer)).isTrue();
        sAssert.assertThat(received).isTrue();
        sAssert.assertAll();
    }

    @Test(expected = JobOfferToBlockingQueueException.class)
    public void shouldThrowJobOfferToBlockingQueueExceptionWhenTryToPutNull() {
        JobOffer jobOffer = null;

        BlockingQueue<JobOffer> bQ = new LinkedBlockingQueue<>(10);
        service.putJobOfferToBlockingQ(bQ, jobOffer);
    }

    @Test
    public void shouldTakeJsonNodeAndPutJobOfferAndFlagAtEndToBlockingQueue() {
        JobOffer jobOffer = new JobOffer("Offer1",
                "13300 - 15000", "id_test1",
                null,
                List.of("SQL", "C", "PHP"),
                "Java");

        JobOffer flagJobOffer = new JobOffer("flag");

        JsonNode jsonNodeSource;
        String path = "src/test/resources/bigJsonTest.json";
        try {
            jsonNodeSource = ObjectMapperHolder.INSTANCE.getMapper().readTree(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonNode jsonNode = jsonNodeSource.get(0);

        JsonNode flagJsonNode;
        try {
            flagJsonNode = ObjectMapperHolder.INSTANCE.getMapper().readTree("{\"end\":\"end\"}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        BlockingQueue<JsonNode> bQN = new LinkedBlockingQueue<>(10);
        BlockingQueue<JobOffer> bQO = new LinkedBlockingQueue<>(10);
        try {
            bQN.put(jsonNode);
            bQN.put(flagJsonNode);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        boolean received = service.takeJsonNodeAndPutJobOffer(bQN, bQO, flagJsonNode, flagJobOffer);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isTrue();
        sAssert.assertThat(bQN.contains(jsonNode)).isFalse();
        sAssert.assertThat(bQN.size()).isEqualTo(1);
        sAssert.assertThat(bQN.contains(flagJsonNode)).isTrue();
        sAssert.assertThat(bQO.contains(jobOffer)).isTrue();
        sAssert.assertThat(bQO.contains(flagJobOffer)).isTrue();
        sAssert.assertThat(bQO.size()).isEqualTo(2);
        sAssert.assertAll();
    }
}