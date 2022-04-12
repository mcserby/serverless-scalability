package eu.accesa.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.List;

import static org.mockito.Mockito.mock;

class WorkerTest {


    @Test
    void batchEtl() throws InterruptedException {
        HttpClient client = mock(HttpClient.class);
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        int workloadDuration = 5;
        Worker worker = new Worker(client, om, workloadDuration);
        worker.batchEtl(List.of(0, 1, 2, 3, 4));
    }
}