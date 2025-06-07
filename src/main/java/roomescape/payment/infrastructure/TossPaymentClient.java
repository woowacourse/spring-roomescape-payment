package roomescape.payment.infrastructure;

import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.UnknownContentTypeException;
import roomescape.exception.payment.PaymentException;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.Payment;

public class TossPaymentClient implements PaymentClient {

    private final String confirmUrl;
    private final String secretKey;
    private final RestClient restClient;

    public TossPaymentClient(
            final String confirmUrl,
            final String secretKey,
            final RestClient restClient
    ) {
        this.confirmUrl = confirmUrl;
        this.secretKey = secretKey;
        this.restClient = restClient;
    }

    public void approvePayment(final Payment payment) {
        final TossPaymentApproveRequest request = TossPaymentApproveRequest.from(payment);
        final String encodedSecretKey = getEncodedSecretKey();

        try {
            restClient.post()
                    .uri(confirmUrl)
                    .header("Authorization", encodedSecretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new PaymentException(
                    GATEWAY_TIMEOUT,
                    "토스 결제 승인 API가 응답하지 않습니다."
            );
        } catch (UnknownContentTypeException e) {
            throw new PaymentException(
                    UNSUPPORTED_MEDIA_TYPE,
                    "토스 결제 승인 API의 응답 형식이 올바르지 않습니다."
            );
        }
    }

    private String getEncodedSecretKey() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
    }
}
