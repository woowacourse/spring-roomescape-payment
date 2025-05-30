package roomescape.payment.infrastructure;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.TossPaymentRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.exception.TossPaymentClientException;
import roomescape.payment.exception.TossPaymentServerException;
import roomescape.payment.exception.TossServerErrorCode;
import roomescape.reservation.dto.response.FailureResponse;

@Component
public class TossApiClient {

    private static final String SECRET_KEY_SUFFIX = ":";
    private static final String AUTHORIZATION_HEADER = "Basic ";
    public static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final RestClient restClient;
    private final String secretKey;

    public TossApiClient(final RestClient restClient, @Value("${toss.secret-key}") final String key) {
        String totalSecretKey = key + SECRET_KEY_SUFFIX;
        secretKey = Base64.getEncoder().encodeToString(totalSecretKey.getBytes());
        this.restClient = restClient;
    }

    public PaymentResponse authPayment(final String paymentKey, final String orderId,
                                       final Integer amount, final String paymentType) {
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount, paymentType);
        try {
            return restClient.post()
                    .uri(PAYMENT_URL)
                    .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + secretKey)
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
            throw new TossPaymentServerException("토스 서버로 문의해주세요.");
        } catch (ResourceAccessException e) {
            throw new TossPaymentServerException("토스 서버 응답이 지연되고 있어요. 잠시 후 다시 시도해주세요.");
        }
    }

}
