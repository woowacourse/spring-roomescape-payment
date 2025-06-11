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
import roomescape.payment.exception.TossPaymentClientException;
import roomescape.payment.exception.TossPaymentServerException;
import roomescape.payment.exception.TossServerErrorCode;
import roomescape.payment.infrastructure.dto.reqeust.PaymentCommand;
import roomescape.payment.infrastructure.dto.reqeust.TossPaymentRequest;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;
import roomescape.reservation.dto.response.FailureResponse;

@Component
public class TossApiClient implements PaymentClient {

    public static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String SECRET_KEY_SUFFIX = ":";
    private static final String AUTHORIZATION_HEADER = "Basic ";
    private final RestClient restClient;
    private final String secretKey;

    public TossApiClient(final RestClient restClient, @Value("${toss.secret-key}") final String key) {
        String totalSecretKey = key + SECRET_KEY_SUFFIX;
        secretKey = Base64.getEncoder().encodeToString(totalSecretKey.getBytes());
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse authPayment(final PaymentCommand paymentCommand) {
        TossPaymentRequest request = new TossPaymentRequest(paymentCommand.paymentKey(), paymentCommand.orderId(),
                paymentCommand.amount(), paymentCommand.paymentType());
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
