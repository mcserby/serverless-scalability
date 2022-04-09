package eu.accesa.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleEtlService implements BackgroundFunction<PubSubMessage> {

    private static final Logger logger = Logger.getLogger(SimpleEtlService.class.getName());

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
            String decodedMessage = new String(Base64.getDecoder().decode(message.data), StandardCharsets.UTF_8);
            logger.info("decodedMessage: " + decodedMessage);
            triggerSimpleEtl();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SIMPLE ETL job cannot be performed. " + e.getMessage(), e);
        }
    }

    private void triggerSimpleEtl() throws InterruptedException {
        int workloadDuration = Integer.parseInt(System.getenv("WORKLOAD_DURATION"));
        SimpleEtlServiceWorker etlService = new SimpleEtlServiceWorker(httpClient, objectMapper, workloadDuration);
        etlService.etl();
    }

}