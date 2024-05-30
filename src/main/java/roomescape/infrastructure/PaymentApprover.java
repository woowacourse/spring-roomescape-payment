package roomescape.infrastructure;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.exception.PaymentException;
import roomescape.exception.PaymentServerException;

@Component
public class PaymentApprover {
    private final RestClient restClient;
    private final PaymentSecretKeyEncoder paymentSecretKeyEncoder;

    public PaymentApprover(RestClient restClient, PaymentSecretKeyEncoder paymentSecretKeyEncoder) {
        this.restClient = restClient;
        this.paymentSecretKeyEncoder = paymentSecretKeyEncoder;
    }

    public PaymentConfirmResponse confirmPayment(final PaymentConfirmRequest paymentRequest) {
        final String authorizations = paymentSecretKeyEncoder.getEncodedSecretKey();

        try {
            return restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorizations)
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentConfirmResponse.class);
        } catch (HttpClientErrorException e) {
            throw PaymentException.from(e);
        } catch (HttpServerErrorException e) {
            throw new PaymentServerException("결제 서버와의 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
        }
    }
}
