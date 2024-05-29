package roomescape.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentException;
import roomescape.service.dto.PaymentRequest;

public class PaymentClient {

    private final RestClient restClient;
    private final PaymentProperties paymentProperties;

    public PaymentClient(RestClient restClient, PaymentProperties paymentProperties) {
        this.restClient = restClient;
        this.paymentProperties = paymentProperties;
    }

    public void requestApproval(PaymentRequest request) {
        String authorizations = createAuthorizationHeader();
        try {
            restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 서버의 연결 가능 시간이 초과되었습니다.");
        }
    }

    private String createAuthorizationHeader() {
        byte[] encodedBytes = Base64.getEncoder()
                .encode((paymentProperties.secretKey() + ":")
                        .getBytes(StandardCharsets.UTF_8));
        return  "Basic " + new String(encodedBytes);
    }
}
