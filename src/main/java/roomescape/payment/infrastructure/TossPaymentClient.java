package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.PaymentClient;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;

@RequiredArgsConstructor
@Component
public class TossPaymentClient implements PaymentClient {

    @Qualifier("tossPaymentRestClient")
    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public PaymentResponse requestPayment(final PaymentRequest request) {
        return restClient.post()
                .uri("v1/payments/confirm")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        (req, res) -> {
                            handleException(res);
                        })
                .body(TossPaymentResponse.class);
    }

    private void handleException(final ClientHttpResponse res) throws IOException {
        final String status = res.getStatusCode().toString();
        final int code = Integer.parseInt(status.substring(0, 3));
        if (code == 401) {
            throw new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "secret key가 유효하지 않습니다");
        }
        throw new TossPaymentException(HttpStatus.BAD_REQUEST, "유효하지 않은 결제방식입니다.");
    }
}

