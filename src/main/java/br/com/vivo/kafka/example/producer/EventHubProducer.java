
package br.com.vivo.kafka.example.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventHubProducer {

    @Value("${event-hub.namespace}")
    private String namespaceName;

    @Value("${event-hub.name}")
    private String eventHubName;

    public ResponseEntity<String> publish(String message) {
        log.info("Publishing message {} into event hub {}", message, eventHubName);

        // create a token using the default Azure credential
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                .build();

        // create a producer client
        EventHubProducerClient producer = new EventHubClientBuilder()
                .fullyQualifiedNamespace(namespaceName)
                .eventHubName(eventHubName)
                .credential(credential)
                .buildProducerClient();

        // create a batch
        EventDataBatch eventDataBatch = producer.createBatch();

        // create the event itself
        EventData eventData = new EventData(message);

        // try to add the event to the batch
        if (!eventDataBatch.tryAdd(eventData)) {
            // if the batch is full, send it and then create a new batch
            producer.send(eventDataBatch);
            eventDataBatch = producer.createBatch();

            // Try to add that event that couldn't fit before.
            if (!eventDataBatch.tryAdd(eventData)) {
                throw new IllegalArgumentException("Event is too large for an empty batch. Max size: "
                        + eventDataBatch.getMaxSizeInBytes());
            }
        }

        // send the last batch of remaining events
        if (eventDataBatch.getCount() > 0) {
            producer.send(eventDataBatch);
        }
        producer.close();

        log.info("Message successfully published");
        return ResponseEntity.ok("Message successfully published");
    }

}