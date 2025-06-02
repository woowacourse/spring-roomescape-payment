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
import roomescape.payment.service.ApiClient;

@Component
public class TossApiClient implements ApiClient {

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
                throw new TossPaymentServerException("서비스에 문제가 발생했습니다. 잠시 후 다시 시도하거나 관리자에게 문의주세요. ");
            }
            throw new TossPaymentClientException(response.message());
        } catch (HttpServerErrorException e) {
            throw new TossPaymentServerException("결제 서버에 문제가 발생했습니다. 잠시 후 다시 시도하거나 다른 결제 수단을 이용하세요");
        }
    }

}
