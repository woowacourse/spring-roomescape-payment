package roomescape.application.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.dto.PaymentCommand;
import roomescape.infrastructure.error.exception.PaymentException;

@Repository
public class TossPaymentClient {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6" + ":";
    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;

    public TossPaymentClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public void approve(PaymentCommand command) {
        String encodedKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + encodedKey)
                .body(command)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode node = mapper.readTree(response.getBody());
                        if (node.has("message")) {
                            log.warn("code: {} message: {}", node.get("code").asText(), node.get("code").asText());
                            throw new PaymentException(node.get("message").asText());
                        }
                    } catch (JsonProcessingException ignored) {
                    }
                    throw new PaymentException("페이먼츠 예외");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new PaymentException("토스 서버 예외");
                })
                .body(String.class);
    }
}
