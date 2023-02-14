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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Value("${rate.limit}")
    private long limitRate;
    private final Map<String, Integer> invokeCountCache = new ConcurrentHashMap<>();
    private final MockMvc mockMvc;
    private final static String URL = "/api/limitedResource";
    private final static String SUCCESS_MESSAGE = "All requests successfully sent";
    private final static int NUMBER_OF_THREADS = 10;
    private final static int NUMBER_OF_USERS = 10;
    private final static int REQUESTS_PER_USER = 10;

    @Autowired
    public ApiControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("Should response with 429 status code when requests are reached limit (5 request per 10 minutes")
    void getResponseTest() {
        try (ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)) {
            List<Future<String>> testResults = new ArrayList<>();
            for (int i = 0; i < NUMBER_OF_USERS; i++) {
                testResults.add(createClient(executorService));
            }
            checkResults(testResults);
        }
    }

    private static void checkResults(List<Future<String>> testResults) {
        testResults.forEach(test -> {
            try {
                String result = test.get();
                assertEquals(SUCCESS_MESSAGE, result);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals(NUMBER_OF_USERS, testResults.size());
    }

    private Future<String> createClient(ExecutorService executorService) {
        String remoteAddress = generateRandomIp();
        invokeCountCache.put(remoteAddress, 1);
        return executorService.submit(() -> startRequesting(remoteAddress));
    }

    private String startRequesting(String remoteAddress) {
        for (int j = 0; j < REQUESTS_PER_USER; j++) {
            sendRequest(remoteAddress);
        }
        return SUCCESS_MESSAGE;
    }

    private void sendRequest(String remoteAddress) {
        try {
            performGetRequest(remoteAddress);
            invokeCountCache.computeIfPresent(remoteAddress, (s, integer) -> ++integer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void performGetRequest(String remoteAddress) throws Exception {
        Integer invokeCount = this.invokeCountCache.get(remoteAddress);
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