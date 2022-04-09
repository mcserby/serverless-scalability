package eu.accesa.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectCallEtlManager implements BackgroundFunction<PubSubMessage> {

    private static final Logger logger = Logger.getLogger(DirectCallEtlManager.class.getName());

    private static final HttpClient httpClient = initHttpClient();

    private static final ObjectMapper objectMapper = initObjectMapper();

    private static HttpClient initHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(60))
                .build();
    }

    private static ObjectMapper initObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }

    @Override
    public void accept(PubSubMessage message, Context context) {
        try {
            directTriggerEtlWorkloads();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ETL job cannot be performed. " + e.getMessage(), e);
        }
    }

    private void directTriggerEtlWorkloads() throws IOException, InterruptedException {
        String projectId = System.getenv("PROJECT_ID");
        String topicId = System.getenv("ETL_WORKER_TOPIC");
        int workerBatchSize = Integer.parseInt(System.getenv("WORKER_BATCH_SIZE"));
        int maxWorkloads = Integer.parseInt(System.getenv("MAX_WORKLOADS"));
        EtlWorkloadTriggerService etlWorkloadTriggerService =
                new EtlWorkloadTriggerService(projectId, topicId, workerBatchSize, maxWorkloads, objectMapper);
        etlWorkloadTriggerService.triggerEtlWorkloads();
    }

}