package interfaces;

import model.JobOffer;

import java.util.List;

public interface SendEmail {
    void sendEmail(List<String> list, JobOffer jobOffer);
}
