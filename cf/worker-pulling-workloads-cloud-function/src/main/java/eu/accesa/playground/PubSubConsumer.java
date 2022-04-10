package eu.accesa.playground;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PubSubConsumer {

    private final Worker worker;
    private final String projectId;
    private final String subscriptionId;
    private final ObjectMapper objectMapper;

    public PubSubConsumer(Worker worker, ObjectMapper objectMapper, String projectId, String subscriptionId) {
        this.worker = worker;
        this.objectMapper = objectMapper;
        this.projectId = projectId;
        this.subscriptionId = subscriptionId;
    }

    public void ingestMessages(int numOfMessages) throws IOException {
        SubscriberStubSettings subscriberStubSettings =
                SubscriberStubSettings.newBuilder()
                        .setTransportChannelProvider(
                                SubscriberStubSettings.defaultGrpcTransportProviderBuilder().build())
                        .build();

        try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings)) {
            String subscriptionName = ProjectSubscriptionName.format(projectId, subscriptionId);
            PullRequest pullRequest =
                    PullRequest.newBuilder()
                            .setMaxMessages(numOfMessages)
                            .setSubscription(subscriptionName)
                            .build();

            PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);
            List<Integer> workloads = new ArrayList<>();
            List<String> ackIds = new ArrayList<>();
            for (ReceivedMessage message : pullResponse.getReceivedMessagesList()) {
                workloads.addAll(extractData(message.getMessage()));
                ackIds.add(message.getAckId());
            }
            acknowledgePubSubMessages(subscriber, subscriptionName, ackIds);
            worker.batchEtl(workloads);
        }
    }

    private void acknowledgePubSubMessages(SubscriberStub subscriber, String subscriptionName, List<String> ackIds) {
        if (!ackIds.isEmpty()) {
            AcknowledgeRequest acknowledgeRequest =
                    AcknowledgeRequest.newBuilder()
                            .setSubscription(subscriptionName)
                            .addAllAckIds(ackIds)
                            .build();
            subscriber.acknowledgeCallable().call(acknowledgeRequest);
        }
    }

    private List<Integer> extractData(PubsubMessage message) throws IOException {
        return objectMapper.readValue(message.getData().toStringUtf8(), new TypeReference<>() {
        });
    }
}