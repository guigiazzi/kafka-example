package br.com.vivo.kafka.example.consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Value;

import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionContext;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

@Slf4j
public class EventHubConsumer {

    @Value("${event-hub.namespace}")
    private String namespaceName;

    @Value("${event-hub.name}")
    private String eventHubName;

    private static final String FIRST_PARTITION = "0";

    private static final int NUMBER_OF_EVENTS = 10;

    public void consume() {
        // create a token using the default Azure credential
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                .build();

        // create a consumer client
        EventHubConsumerAsyncClient consumer = new EventHubClientBuilder()
                .fullyQualifiedNamespace(namespaceName)
                .eventHubName(eventHubName)
                .credential(credential)
                .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                .buildAsyncConsumerClient();

        CountDownLatch countDownLatch = new CountDownLatch(NUMBER_OF_EVENTS);

        Disposable subscription = consumer.receiveFromPartition(FIRST_PARTITION, EventPosition.latest())
                .subscribe(partitionEvent -> {
                    EventData event = partitionEvent.getData();
                    PartitionContext partitionContext = partitionEvent.getPartitionContext();

                    String contents = new String(event.getBody(), UTF_8);
                    log.info("#{} Partition id: {}. Sequence Number: {}. Contents: {}{}",
                            countDownLatch.getCount(), partitionContext.getPartitionId(), event.getSequenceNumber(),
                            contents);

                    countDownLatch.countDown();
                },
                        error -> {
                            log.error("Error occurred while consuming events: {}", error);

                            // Count down until 0, so the main thread does not keep waiting for events.
                            while (countDownLatch.getCount() > 0) {
                                countDownLatch.countDown();
                            }
                        }, () -> {
                            log.info("Finished reading events");
                        });

        subscription.dispose();
    }

}
