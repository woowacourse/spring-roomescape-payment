package roomescape.infrastructure.payment;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.payment.TossErrorResponse;
import roomescape.exception.PaymentException;
import roomescape.exception.PaymentInternalException;
import roomescape.util.LogSaver;

/**
 * @see <a href="https://docs.tosspayments.com/reference/error-codes">토스 결제 오류 코드 정의서</a>
 */
public class TossPaymentClient {

    @Value("${secret-key}")
    private String secretKey;
    private final RestClient restClient;
    private final LogSaver logSaver;

    public TossPaymentClient(final RestClient restClient, final LogSaver logSaver) {
        this.restClient = restClient;
        this.logSaver = logSaver;
    }

    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        String authorizationKey = secretKey + ":";

        try {
            PaymentResponse paymentResponse = restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString(authorizationKey.getBytes()))
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);

            logSaver.logInfo(paymentRequest);
            logSaver.logInfo(paymentResponse);
            return paymentResponse;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            TossErrorResponse errorResponse = getErrorResponse(e);
            HttpStatusCode clientStatusCode = TossErrorHandler.covertStatusCode(e.getStatusCode(),
                    errorResponse.code());
            throw new PaymentException(
                    e.getClass().getName(),
                    e.getStatusCode(),
                    clientStatusCode,
                    errorResponse.message(),
                    paymentRequest.paymentKey()
            );

        } catch (Exception e) {
            throw new PaymentInternalException(e.getClass().getName(), "시스템에서 오류가 발생했습니다.");
        }
    }

    private TossErrorResponse getErrorResponse(final HttpStatusCodeException exception) {
        try {
            return exception.getResponseBodyAs(TossErrorResponse.class);
        } catch (RestClientResponseException e) {
            throw new PaymentInternalException(e.getClass().getName(), "결제 오류 객체를 생성하는 과정에서 오류가 발생했습니다.");
        }
    }
}

