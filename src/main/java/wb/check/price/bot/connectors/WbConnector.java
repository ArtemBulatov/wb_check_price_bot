package wb.check.price.bot.connectors;

import wb.check.price.bot.dto.WbCardDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class WbConnector {
    private final RestTemplate restTemplate;
    private final Gson gson;

    public WbConnector() {
        this.restTemplate = new RestTemplate();
        this.gson = new Gson();
    }

    public WbCardDTO getProductFromWb(long productId) {
        ResponseEntity<String> response = restTemplate
                .getForEntity("https://card.wb.ru/cards/detail?nm=" + productId, String.class);

        if (response.hasBody()) {
            try {
                return gson.fromJson(response.getBody(), WbCardDTO.class);
            } catch (Exception e) {
                log.error("getProductFromWb: " + e.getMessage());
            }
        }
        return null;
    }

}
