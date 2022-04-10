package eu.accesa.playground;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.util.List;
import java.util.logging.Logger;

public class Worker {

    private static final Logger logger = Logger.getLogger(Worker.class.getName());

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final int workloadDuration;

    public Worker(HttpClient httpClient, ObjectMapper objectMapper, int workloadDuration) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.workloadDuration = workloadDuration;
    }

    public void batchEtl(List<Integer> workloads) {
        try {
            logger.info("processing workloads: " + workloads);
            for (Integer workload : workloads) {
                etl(workload);
            }
            logger.info("processing complete.");
        } catch (Exception e){
            logger.severe("could not process batch workloads: " + workloads);
        }
    }

    public void etl(Integer workload) throws InterruptedException {
        logger.info("starting etl workload " + workload);
        Thread.sleep(workloadDuration * 1000L);
        logger.info("etl workload " + workload + " complete");
    }
}
