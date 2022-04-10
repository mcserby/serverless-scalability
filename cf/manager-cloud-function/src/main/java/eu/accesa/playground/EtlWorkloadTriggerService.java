package eu.accesa.playground;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EtlWorkloadTriggerService {

    private static final Logger logger = Logger.getLogger(EtlWorkloadTriggerService.class.getName());
    private final String projectId;
    private final String topicId;
    private final int workerBatchSize;
    private final int maxWorkloads;
    private final ObjectMapper objectMapper;

    public EtlWorkloadTriggerService(String projectId, String topicId, int workerBatchSize, int maxWorkloads, ObjectMapper objectMapper) {
        this.projectId = projectId;
        this.topicId = topicId;
        this.workerBatchSize = workerBatchSize;
        this.maxWorkloads = maxWorkloads;
        this.objectMapper = objectMapper;
    }


    public void triggerEtlWorkloads() throws InterruptedException, IOException {
        List<Integer> workloads = IntStream.range(0, new Random().nextInt(this.maxWorkloads))
                .boxed()
                .collect(Collectors.toList());
        List<List<Integer>> partitions = Lists.partition(workloads, this.workerBatchSize);
        Publisher publisher = Publisher.newBuilder(TopicName.of(projectId, topicId)).build();
        try {
            for (List<Integer> partition : partitions) {
                sendEtlNotification(publisher, partition);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot publish PUBSUB message with ETL workload. ", e);
        } finally {
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
        }
        logger.info(partitions.size() + " worker notifications triggered.");
    }

    private void sendEtlNotification(Publisher publisher, List<Integer> articleFacades)
            throws ExecutionException, InterruptedException, JsonProcessingException {
        String message = objectMapper.writeValueAsString(articleFacades);
        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        messageIdFuture.get();
    }

}
