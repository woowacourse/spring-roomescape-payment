package roomescape.infrastructure.payment;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import roomescape.business.dto.PaymentApproveDto;
import roomescape.exception.PaymentApproveException;

public class TossPaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public PaymentApproveResponseDto approvePayment(PaymentApproveDto paymentApproveDto) {
        Map<String, Object> body = Map.of(
                "orderId", paymentApproveDto.orderId(),
                "paymentKey", paymentApproveDto.paymentKey(),
                "amount", paymentApproveDto.amount()
        );
        String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6하하";
        String encodedString = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(body)
                .header("Authorization", "Basic " + encodedString)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    InputStream bodyStream = response.getBody();
                    String rawJson = StreamUtils.copyToString(bodyStream, StandardCharsets.UTF_8);
                    PaymentApproveException err = objectMapper.readValue(rawJson, PaymentApproveException.class);
                    throw err;
                })
                .toEntity(PaymentApproveResponseDto.class).getBody();
    }
}
