package projektZajavka2.client;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



// @AllArgsConstructor
@Component
public class NBPClient {
    private static final String API_URL = "https://api.nbp.pl/api/exchangerates/rates/A/USD?format=json";

    private final RestTemplate restTemplate;

    @Autowired
    public NBPClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getUsdRate() {
        return restTemplate.getForObject(API_URL, String.class);

    }
}
