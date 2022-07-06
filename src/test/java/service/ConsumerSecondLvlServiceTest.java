package service;

import model.JobOffer;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerSecondLvlServiceTest {
    private ConsumerSecondLvlService service;

    @Mock
    DatabaseService databaseServiceMock;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        service = new ConsumerSecondLvlService();
    }

    @Test
    public void shouldTakeJobOfferFromBlockingQ() {
        JobOffer jobOffer = new JobOffer("Offer1",
                "13300 - 15000", "id_test1",
                null,
                List.of("SQL", "C", "PHP"),
                "Java");

        BlockingQueue<JobOffer> bQ = new LinkedBlockingQueue<>(10);
        try {
            bQ.put(jobOffer);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        JobOffer received = service.takeJobOfferFromBlockingQ(bQ);

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(bQ.contains(jobOffer)).isFalse();
        sAssert.assertThat(received).isEqualTo(jobOffer);
        sAssert.assertThat(bQ.isEmpty()).isTrue();
        sAssert.assertAll();
    }

    @Test
    public void shouldPutFlagToJobOfferBlockingQ() {
        JobOffer flag = new JobOffer("flag");
        BlockingQueue<JobOffer> bQ = new LinkedBlockingQueue<>(10);

        boolean received = service.putFlagToJobOfferBlockingQ(bQ, flag);
        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isTrue();
        sAssert.assertThat(bQ.isEmpty()).isFalse();
        sAssert.assertThat(bQ.contains(flag)).isTrue();
        sAssert.assertAll();
    }

    @Test
    public void shouldTakeJobOffersFormBlockingQAndSendEmailsFormDatabase() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        JobOffer jobOffer = new JobOffer("Offer1",
                "13300 - 15000", "id_test1",
                null,
                List.of("SQL", "C", "PHP"),
                "Java");
        JobOffer flag = new JobOffer("flag");

        BlockingQueue<JobOffer> bQ = new LinkedBlockingQueue<>(10);
        try {
            bQ.put(jobOffer);
            bQ.put(flag);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String query = "test";
        List<String> emails = List.of("p1@gmail.com", "p5@gmail.com");
        String expectedSOUT = "Offer1 send to [p1@gmail.com, p5@gmail.com]";

        Mockito.when(databaseServiceMock.prepareQueryForLanguage(jobOffer)).thenReturn(query);
        Mockito.when(databaseServiceMock.getEmailsListFromDb(query)).thenReturn(emails);

        boolean received = service.takeJobOffersFromBlockingQAndSendEmails(bQ, flag, databaseServiceMock);
        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(received).isTrue();
        sAssert.assertThat(bQ.contains(jobOffer)).isFalse();
        sAssert.assertThat(bQ.contains(flag)).isTrue();
        sAssert.assertThat(outputStreamCaptor.toString().trim()).isEqualTo(expectedSOUT);
        sAssert.assertAll();
    }


}