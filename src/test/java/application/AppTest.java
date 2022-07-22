package application;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import service.DatabaseService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class AppTest {

    private App app;

    @Mock
    DatabaseService dbServiceMock;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        app = new App();
    }

    @Test
    public void shouldRunCorrectlyAppWhereProducersNumberMultipliedByLoopForThemIsOverSourceJsonSize() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        String queryJava = "queryJava";
        String queryPython = "queryPython";
        String queryC = "queryC";
        String json = "src/test/resources/bigJsonTest.json";

        List<String> emailsJava = List.of("java1@gmail.com", "java2@gmail.com");
        List<String> emailsPython = List.of("python@gmail.com");
        List<String> emailsC = List.of("c@gmail.com");

        Mockito.when(dbServiceMock.prepareQueryForLanguage("Java")).thenReturn(queryJava);
        Mockito.when(dbServiceMock.prepareQueryForLanguage("Python")).thenReturn(queryPython);
        Mockito.when(dbServiceMock.prepareQueryForLanguage("c")).thenReturn(queryC);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryJava)).thenReturn(emailsJava);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryPython)).thenReturn(emailsPython);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryC)).thenReturn(emailsC);

        app.run(3, 4, 4, 2, 2, json, dbServiceMock);

        while (true){
            if (app.getExecutorService().isTerminated()){
                break;
            }
        }

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer1Java sent to " + emailsJava);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer2Python sent to " + emailsPython);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer3Java sent to " + emailsJava);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer4C sent to " + emailsC);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer5C sent to " + emailsC);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer6Java sent to " + emailsJava);
        sAssert.assertAll();
    }

    @Test
    public void shouldRunAppCorrectlyWhereProducersNumberMultipliedByLoopForThemIsExactlyAsSourceJsonSize() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        String queryJava = "queryJava";
        String queryPython = "queryPython";
        String queryC = "queryC";
        String json = "src/test/resources/bigJsonTest.json";

        List<String> emailsJava = List.of("java1@gmail.com", "java2@gmail.com");
        List<String> emailsPython = List.of("python@gmail.com");
        List<String> emailsC = List.of("c@gmail.com");

        Mockito.when(dbServiceMock.prepareQueryForLanguage("Java")).thenReturn(queryJava);
        Mockito.when(dbServiceMock.prepareQueryForLanguage("Python")).thenReturn(queryPython);
        Mockito.when(dbServiceMock.prepareQueryForLanguage("c")).thenReturn(queryC);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryJava)).thenReturn(emailsJava);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryPython)).thenReturn(emailsPython);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryC)).thenReturn(emailsC);

        app.run(2, 4, 3, 2, 2, json, dbServiceMock);
        while (true){
            if (app.getExecutorService().isTerminated()){
                break;
            }
        }

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer1Java sent to " + emailsJava);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer2Python sent to " + emailsPython);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer3Java sent to " + emailsJava);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer4C sent to " + emailsC);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer5C sent to " + emailsC);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer6Java sent to " + emailsJava);
        sAssert.assertAll();
    }

    @Test
    public void shouldRunAppCorrectlyWhereProducersNumberMultipliedByLoopForThemIsBelowSourceJsonSize() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        String queryJava = "queryJava";
        String queryPython = "queryPython";
        String queryC = "queryC";
        String json = "src/test/resources/bigJsonTest.json";

        List<String> emailsJava = List.of("java1@gmail.com", "java2@gmail.com");
        List<String> emailsPython = List.of("python@gmail.com");
        List<String> emailsC = List.of("c@gmail.com");

        Mockito.when(dbServiceMock.prepareQueryForLanguage("Java")).thenReturn(queryJava);
        Mockito.when(dbServiceMock.prepareQueryForLanguage("Python")).thenReturn(queryPython);
        Mockito.when(dbServiceMock.prepareQueryForLanguage("c")).thenReturn(queryC);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryJava)).thenReturn(emailsJava);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryPython)).thenReturn(emailsPython);
        Mockito.when(dbServiceMock.getEmailsListFromDb(queryC)).thenReturn(emailsC);

        app.run(2, 4, 2, 2, 2, json, dbServiceMock);
        while (true){
            if (app.getExecutorService().isTerminated()){
                break;
            }
        }

        SoftAssertions sAssert = new SoftAssertions();
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer1Java sent to " + emailsJava);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer2Python sent to " + emailsPython);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer3Java sent to " + emailsJava);
        sAssert.assertThat(outputStreamCaptor.toString()).contains("Offer4C sent to " + emailsC);
        sAssert.assertThat(outputStreamCaptor.toString()).doesNotContain("Offer5C sent to " + emailsC);
        sAssert.assertThat(outputStreamCaptor.toString()).doesNotContain("Offer6Java sent to " + emailsJava);
        sAssert.assertAll();
    }
}