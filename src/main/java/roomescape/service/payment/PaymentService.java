package roomescape.service.payment;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

@Service
public class PaymentService {

    // TODO : 나중에 빈으로 관리, baseURL 세팅
    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    /*
        정상 흐름 -> OK
        먼가 예외 -> 예외 발생시킨다.
         */
    public void approvePayment(String paymentKey, String orderId, int amount) {
        String widgetSecretKey = "test_sk_nRQoOaPz8LK6ZxMR2BZNry47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);
        final Map<String, Object> requestBody = Map.of(
                "amount", amount,
                "orderId", orderId,
                "paymentKey", paymentKey
        );

        final Payment payment = restClient.post().uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", authorizations)
                .body(requestBody)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK,
                        (request, response) -> {
                            throw new IllegalStateException("[ERROR] 결제 승인 중 예외가 발생하였습니다.");
                        }
                ).body(Payment.class);

        // TODO : payment 저장
    }
}
