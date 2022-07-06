package service;

import connection.DBCPDataSource;
import exceptions.EmailListFromDatabaseException;
import model.JobOffer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    public String prepareQueryForLanguage(JobOffer jobOffer) {
        String language = jobOffer.getMainProgrammingLanguage();
        String query = "";
        query = "SELECT email FROM programmer " +
                "WHERE id " +
                "IN (SELECT idprogrammer FROM interested " +
                "WHERE idlanguage = (SELECT id FROM language " +
                "WHERE name = '" + language + "')); ";
        return query;
    }

    public List<String> getEmailsListFromDb(String query) {
        List<String> emails = new ArrayList<>();
        try (Connection connection = DBCPDataSource.getConnection();
             Statement stm = connection.createStatement();) {
            try (ResultSet resultSet = stm.executeQuery(query)) {
                while (resultSet.next()) {
                    emails.add(resultSet.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new EmailListFromDatabaseException(e.getMessage());
        }
        return emails;
    }
}
