package roomescape.integrate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@ActiveProfiles("test")
class TossPaymentApiTest {

    private static final String TOSS_API_URL = "https://api.tosspayments.com";
    private static final String CONFIRM_PATH = "/v1/payments/confirm";

    @Autowired
    private RestClient restClient;

    @Value("${toss.payment.secret-key}")
    private String tossSecretKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Disabled
    @Test
    void 토스_결제_승인_API를_호출하여_결제를_승인한다() throws Exception {
        // given
        String paymentKey = "tgen_20250603193203Xkwj3";
        String orderId = "MC40MDI1MDU4MjM3NTI2";
        Long amount = 50000L;

        String requestBody = createConfirmRequestBody(paymentKey, orderId, amount);
        String encodedSecretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        // when
        String response = restClient.post()
                .uri(TOSS_API_URL + CONFIRM_PATH)
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        // then
        assertThat(response).isNotNull();

        // 응답 JSON 파싱하여 검증
        JsonNode responseJson = objectMapper.readTree(response);
        assertThat(responseJson.get("status").asText()).isEqualTo("DONE");
        assertThat(responseJson.get("totalAmount").asLong()).isEqualTo(amount);
        assertThat(responseJson.get("orderId").asText()).isEqualTo(orderId);
    }

    @Test
    void 잘못된_결제_정보로_토스_결제_승인_API_호출시_에러가_발생한다() {
        // given
        String paymentKey = "invalid_payment_key";
        String orderId = "invalid_order_id";
        Long amount = 0L;

        String requestBody = createConfirmRequestBody(paymentKey, orderId, amount);
        String encodedSecretKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThatThrownBy(() ->
                restClient.post()
                        .uri(TOSS_API_URL + CONFIRM_PATH)
                        .header("Authorization", "Basic " + encodedSecretKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(String.class)
        ).isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> {
                    HttpClientErrorException ex = (HttpClientErrorException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    private String createConfirmRequestBody(String paymentKey, String orderId, Long amount) {
        return """
            {
                "paymentKey": "%s",
                "orderId": "%s",
                "amount": %d
            }
            """.formatted(paymentKey, orderId, amount);
    }
}
