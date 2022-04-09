package eu.accesa.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SimpleEtlServiceWorkerTest {

    @Test
    void etl() throws InterruptedException {
        HttpClient client = mock(HttpClient.class);
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        int workloadDuration = 10;
        SimpleEtlServiceWorker worker = new SimpleEtlServiceWorker(client, om, workloadDuration);
        worker.etl();
    }
}