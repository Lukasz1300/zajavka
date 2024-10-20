package projektZajavka2.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;

    private WireMockServer wireMockServer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Uruchomienie WireMock
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();

        wireMockServer.stubFor(get(urlEqualTo("/exchangerates/rates/A/USD?format=json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"rates\": [{ \"mid\": \"4.05\" }] }")));

        currencyService = new CurrencyService(restTemplate, "http://localhost:8080", objectMapper);
    }

    @Test
    public void testGetUsdExchangeRate() {
        String rate = currencyService.getUsdExchangeRate();
        assertEquals("4.05", rate);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }
}
