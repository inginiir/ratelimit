package ru.kalita.ratelimit.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.kalita.ratelimit.controllers.TestUtils.generateRandomIp;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
class ApiControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;
    private final Map<String, Integer> invokeCount = new ConcurrentHashMap<>();
    private final static String URL = "/api/limitedResource";
    private final MockMvc mockMvc;
    @Value("${rate.limit}")
    private long limitRate;

    @Autowired
    public ApiControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("Should return 429 when requests are reached limit (5 request per 10 minute")
    void getResponseTest() {
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 10; i++) {
                createClient(executorService);
            }
        }
    }

    private void createClient(ExecutorService executorService) {
        String remoteAddress = generateRandomIp();
        invokeCount.put(remoteAddress, 1);
        executorService.submit(() -> startRequesting(remoteAddress));
    }

    private void startRequesting(String remoteAddress) {
        for (int j = 0; j < 10; j++) {
            sendRequest(remoteAddress);
        }
    }

    private void sendRequest(String remoteAddress) {
        try {
            performGetRequest(remoteAddress);
            invokeCount.computeIfPresent(remoteAddress, (s, integer) -> ++integer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void performGetRequest(String remoteAddress) throws Exception {
        Integer invokeCount = this.invokeCount.get(remoteAddress);
        System.out.printf("Отправка запроса %d пользователем %s c %s%n", invokeCount, Thread.currentThread().getName(), remoteAddress);
        int status = mockMvc.perform(get(URL).with(request -> {
                    request.setRemoteAddr(remoteAddress);
                    return request;
                }))
                .andExpect(resolveExpectedStatus(invokeCount))
                .andReturn().getResponse().getStatus();
        System.out.printf("Вызов %d для %s завершился с кодом %d%n", invokeCount, remoteAddress, status);
    }

    private ResultMatcher resolveExpectedStatus(Integer invokeCount) {
        return invokeCount <= limitRate ? status().isOk() :
                status().isTooManyRequests();
    }
}