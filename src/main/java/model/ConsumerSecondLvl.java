package model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import service.ConsumerSecondLvlService;
import service.DatabaseService;

import java.util.concurrent.BlockingQueue;

@Data
@RequiredArgsConstructor
public class ConsumerSecondLvl implements Runnable {
    @NonNull
    private BlockingQueue<JobOffer> blockingQueue;
    @NonNull
    private JobOffer flagJobOffer;
    @NonNull
    DatabaseService databaseService;
    private ConsumerSecondLvlService cons2Service = new ConsumerSecondLvlService();

    @Override
    public void run() {
        cons2Service.takeJobOffersFromBlockingQAndSendEmails(blockingQueue, flagJobOffer, databaseService);
    }
}
