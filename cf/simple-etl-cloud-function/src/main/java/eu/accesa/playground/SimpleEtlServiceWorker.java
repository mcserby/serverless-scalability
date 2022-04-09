package eu.accesa.playground;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.util.logging.Logger;

public class SimpleEtlServiceWorker {

    private static final Logger logger = Logger.getLogger(SimpleEtlServiceWorker.class.getName());

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final int workloadDuration;

    public SimpleEtlServiceWorker(HttpClient httpClient, ObjectMapper objectMapper, int workloadDuration) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.workloadDuration = workloadDuration;
    }

    public void etl() throws InterruptedException {
        logger.info("starting simple etl job..");
        Thread.sleep(workloadDuration * 1000L);
        logger.info("etl job complete.");
    }
}
