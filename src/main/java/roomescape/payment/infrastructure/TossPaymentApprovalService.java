package roomescape.payment.infrastructure;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.PaymentApprovalService;

//TODO: restClient 빈등록 여부 고민하기  (2025-05-28, 수, 14:50)
//TODO: 에러 핸들링 하기!!  (2025-05-28, 수, 15:5)
//TODO: 왜 지금 베이스, 헤더 지정이 안되지..?  (2025-05-28, 수, 15:32)
@Component
public class TossPaymentApprovalService implements PaymentApprovalService {

    @Value("${toss.secretKey}")
    private String SECRET_KEY;
    @Value("${toss.baseUrl}")
    private String BASE_URL;

    private final RestClient restClient = RestClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader("Authorization", basicAuthHeader(SECRET_KEY))
            .defaultHeader("Content-Type", "application/json")
            .build();

    private String basicAuthHeader(String secretKey) {
        String credentials = secretKey + ":";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    @Override
    public void approvePayment(String orderId, BigDecimal amount, String paymentKey) {
        System.out.println(SECRET_KEY);
        System.out.println(BASE_URL);
        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "amount", amount,
                "paymentKey", paymentKey
        );

        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", basicAuthHeader(SECRET_KEY))
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
