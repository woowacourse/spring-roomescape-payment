package roomescape.infrastructure.payment.toss;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.dto.payment.CancelRequest;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.payment.TossErrorResponse;
import roomescape.exception.custom.PaymentCancelException;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.custom.PaymentInternalException;
import roomescape.util.LogSaver;

/**
 * @see <a href="https://docs.tosspayments.com/reference/error-codes">토스 결제 오류 코드 정의서</a>
 */
public class TossPaymentClient {

    private final RestClient restClient;
    private final LogSaver logSaver;

    public TossPaymentClient(final RestClient restClient, final LogSaver logSaver) {
        this.restClient = restClient;
        this.logSaver = logSaver;
    }

    public PaymentResponse confirm(PaymentRequest paymentRequest) {

        try {
            PaymentResponse paymentResponse = restClient.post()
                    .uri("/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);

            logSaver.logInfo("토스 결제 API 호출 요청 Json", paymentRequest);
            logSaver.logInfo("토스 결제 API 호출 응답 Json", paymentResponse);
            return paymentResponse;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            TossErrorResponse errorResponse = getErrorResponse(e);
            HttpStatusCode clientStatusCode = TossErrorHandler.covertStatusCode(e.getStatusCode(), errorResponse.code());
            throw new PaymentException(e, clientStatusCode, errorResponse.message(), paymentRequest);

        } catch (ResourceAccessException e) {
            throw new PaymentException(e, HttpStatus.INTERNAL_SERVER_ERROR, "요청 시간을 초과하였습니다.", paymentRequest);

        } catch (Exception e) {
            throw new PaymentInternalException(e, "시스템에서 오류가 발생했습니다.");
        }
    }

    public PaymentResponse cancel(final CancelRequest cancelRequest, final PaymentResponse paymentResponse) {
        try {
            PaymentResponse cancelResponse = restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/{paymentKey}/cancel").build(paymentResponse.paymentKey()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cancelRequest)
                    .retrieve()
                    .body(PaymentResponse.class);

            logSaver.logInfo("토스 취소 API 호출 요청 Json", cancelRequest);
            logSaver.logInfo("토스 취소 API 호출 응답 Json", cancelResponse);
            return cancelResponse;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            TossErrorResponse errorResponse = getErrorResponse(e);
            HttpStatusCode clientStatusCode = TossErrorHandler.covertStatusCode(e.getStatusCode(), errorResponse.code());
            throw new PaymentCancelException(e, clientStatusCode, errorResponse.message(), cancelRequest);

        } catch (ResourceAccessException e) {
            throw new PaymentCancelException(e, HttpStatus.INTERNAL_SERVER_ERROR, "요청 시간을 초과하였습니다.", cancelRequest);

        } catch (Exception e) {
            throw new PaymentInternalException(e, "시스템에서 오류가 발생했습니다.");
        }
    }

    private TossErrorResponse getErrorResponse(final HttpStatusCodeException exception) {
        try {
            return exception.getResponseBodyAs(TossErrorResponse.class);
        } catch (RestClientResponseException e) {
            throw new PaymentInternalException(e, "결제 오류 객체를 생성하는 과정에서 오류가 발생했습니다.");
        }
    }
}

