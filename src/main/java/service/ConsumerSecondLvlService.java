package service;

import exceptions.JobOfferToBlockingQueueException;
import exceptions.JobOfferFromBlockingQueueException;
import interfaces.SendEmail;
import model.JobOffer;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ConsumerSecondLvlService implements SendEmail {
    public JobOffer takeJobOfferFromBlockingQ(BlockingQueue<JobOffer> blockingQ) {
        JobOffer jobOffer = null;
        try {
            jobOffer = blockingQ.take();
        } catch (InterruptedException e) {
            throw new JobOfferFromBlockingQueueException(e.getMessage());
        }
        return jobOffer;
    }

    public boolean isJobOfferFlag(JobOffer currentJobOffer,
                                  JobOffer flagJobOffer) {
        boolean isFlag = false;
        if (currentJobOffer.equals(flagJobOffer)) {
            isFlag = true;
//            System.out.println("koniec C2 " + Thread.currentThread().getName());
        }
        return isFlag;
    }

    public boolean putFlagToJobOfferBlockingQ(BlockingQueue<JobOffer> blockingQJobOffer,
                                              JobOffer flagJobOffer) {
        try {
            blockingQJobOffer.put(flagJobOffer);
            return true;
        } catch (InterruptedException e) {
            throw new JobOfferToBlockingQueueException(e.getMessage());
        }
    }

    public List<String> getFromDatabaseAppropriateEmailsListForJobOffer(JobOffer jobOffer,
                                                                        DatabaseService databaseService) {
        String query = databaseService.prepareQueryForLanguage(jobOffer);
        return databaseService.getEmailsListFromDb(query);
    }

    public boolean takeJobOffersFromBlockingQAndSendEmails(BlockingQueue<JobOffer> blockingQJobOffer,
                                                           JobOffer flagJobOffer,
                                                           DatabaseService databaseService) {
        while (true) {
            JobOffer jobOffer = takeJobOfferFromBlockingQ(blockingQJobOffer);
            if (isJobOfferFlag(jobOffer, flagJobOffer)) {
                putFlagToJobOfferBlockingQ(blockingQJobOffer, flagJobOffer);
                return true;
            }
            List<String> emails = getFromDatabaseAppropriateEmailsListForJobOffer(jobOffer, databaseService);
            if (emails.size() > 0) {
                sendEmail(emails, jobOffer);
            }

        }
    }

    @Override
    public void sendEmail(List<String> list, JobOffer jobOffer) {
        System.out.println(jobOffer.getTitle() + " send to " + list);
    }
}
