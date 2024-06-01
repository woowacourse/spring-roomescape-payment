package roomescape.infrastructure.payment;

import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.payment.TossErrorResponse;
import roomescape.exception.PaymentException;

/**
 * @see <a href="https://docs.tosspayments.com/reference/error-codes">토스 결제 오류 코드 정의서</a>
 */
@Component
public class TossPaymentClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${secret-key}")
    private String secretKey;
    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        String authorizationKey = secretKey + ":";

        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString(authorizationKey.getBytes()))
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            TossErrorResponse errorResponse = getErrorResponse(e);
            log.error("[{}] statusCode: {}, message: {}, paymentKey: {}",
                    e.getClass().getName(),
                    e.getStatusCode(),
                    errorResponse.message(),
                    paymentRequest.paymentKey()
            );
            HttpStatusCode statusCode = TossErrorHandler.covertStatusCode(e.getStatusCode(), errorResponse.code());
            throw new PaymentException(errorResponse.message(), statusCode);

        } catch (Exception e) {
            log.error("[{}] {}",
                    e.getClass().getName(),
                    "시스템에서 오류가 발생했습니다."
            );
            throw new PaymentException("시스템에서 오류가 발생했습니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private TossErrorResponse getErrorResponse(final HttpStatusCodeException exception) {
        try {
            return exception.getResponseBodyAs(TossErrorResponse.class);
        } catch (RestClientResponseException e) {
            log.error("[{}] {}",
                    e.getClass().getName(),
                    "결제 오류 객체를 생성하는 과정에서 오류가 발생했습니다."
            );
            throw new PaymentException("시스템에서 오류가 발생했습니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
