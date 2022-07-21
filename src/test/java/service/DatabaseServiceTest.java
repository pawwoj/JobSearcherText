package service;

import model.JobOffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class DatabaseServiceTest {

    private DatabaseService databaseService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        databaseService = new DatabaseService();
    }

    @Test
    public void shouldReturnStringAsExpected() {
        String expected = "SELECT email FROM programmer " +
                "WHERE id " +
                "IN (SELECT idprogrammer FROM interested " +
                "WHERE idlanguage = (SELECT id FROM language " +
                "WHERE name = 'Java')); ";
        assertEquals(databaseService.prepareQueryForLanguage("Java"), expected);
    }
}