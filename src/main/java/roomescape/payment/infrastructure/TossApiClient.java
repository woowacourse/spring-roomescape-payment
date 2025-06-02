package roomescape.payment.infrastructure;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.TossPaymentRequest;
import roomescape.payment.dto.response.FailureResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.exception.TossPaymentClientException;
import roomescape.payment.exception.TossPaymentServerException;
import roomescape.payment.exception.TossServerErrorCode;

@Component
public class TossApiClient {

    private static final String SECRET_KEY_SUFFIX = ":";
    private static final String AUTHORIZATION_HEADER = "Basic ";
    public static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final RestClient restClient;

    public TossApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse authPayment(final String paymentKey, final String orderId,
                                       final Integer amount, final String paymentType) {
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount, paymentType);
        try {
            return restClient.post()
                    .uri(PAYMENT_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(PaymentResponse.class)
                    .getBody();
        } catch (HttpClientErrorException e) {
            FailureResponse response = e.getResponseBodyAs(FailureResponse.class);
            if (TossServerErrorCode.isServerError(response.code())) {
                throw new TossPaymentServerException("서버 오류입니다. 서버 관리자한테 문의해주세요.");
            }
            throw new TossPaymentClientException(response.message());
        } catch (HttpServerErrorException e) {
            throw new TossPaymentServerException("토스 서버로 문의해주세요");
        }
    }

}
