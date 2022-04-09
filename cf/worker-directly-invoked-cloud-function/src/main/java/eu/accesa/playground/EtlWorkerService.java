package eu.accesa.playground;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EtlWorkerService implements BackgroundFunction<PubSubMessage> {

    private static final Logger logger = Logger.getLogger(EtlWorkerService.class.getName());

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
            List<Integer> workloads = extractData(message);
            logger.info("processing workloads: " + workloads);
            int workloadDuration = Integer.parseInt(System.getenv("WORKLOAD_DURATION"));
            Worker worker = new Worker(httpClient, objectMapper, workloadDuration);
            worker.batchEtl(workloads);
            logger.info("processing complete.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ETL WORKER job cannot be performed. " + e.getMessage(), e);
        }
    }

    private List<Integer> extractData(PubSubMessage message) throws IOException {
        if (message.data == null) {
            throw new RuntimeException("data missing from PUB SUB message");
        }
        return objectMapper.readValue(Base64.getDecoder().decode(message.data), new TypeReference<>() {});
    }

}