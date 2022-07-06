package model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import service.ConsumerFirstLvlService;

import java.util.concurrent.BlockingQueue;

@Data
@RequiredArgsConstructor
public class ConsumerFirstLvl implements Runnable {

    @NonNull
    private BlockingQueue<JsonNode> blockingQJsonNode;
    @NonNull
    private BlockingQueue<JobOffer> blockingQJobOffer;
    @NonNull
    private JsonNode flagJsonNode;
    @NonNull
    private JobOffer flagJobOffer;
    private ConsumerFirstLvlService consService = new ConsumerFirstLvlService();

    @Override
    public void run() {
        consService.takeJsonNodeAndPutJobOffer(
                blockingQJsonNode,
                blockingQJobOffer,
                flagJsonNode,
                flagJobOffer);
    }
}
