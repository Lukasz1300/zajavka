package projektZajavka2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Data
@Service
@Slf4j
public class CurrencyService {

    private final RestTemplate restTemplate;
    private final String externalApiUrl;
    private final ObjectMapper objectMapper; // Dodano

    // Zaktualizowany konstruktor
    public CurrencyService(RestTemplate restTemplate,
                           @Value("${currency.api.url}") String externalApiUrl,
                           ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.externalApiUrl = externalApiUrl;
        this.objectMapper = objectMapper; // Wstrzyknięcie ObjectMapper
    }

    public String getUsdExchangeRate() {
        // Sprawdź, czy externalApiUrl jest ustawiony
        if (externalApiUrl == null || externalApiUrl.isEmpty()) {
            throw new IllegalStateException("External API URL is not set");
        }
        String url = externalApiUrl + "/exchangerates/rates/A/USD?format=json";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Analizowanie odpowiedzi JSON i zwrócenie kursu
        return parseRate(response.getBody());
    }

    private String parseRate(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody); // Użycie wstrzykniętego ObjectMapper
            return root.path("rates").get(0).path("mid").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse exchange rate", e);
        }
    }
}
