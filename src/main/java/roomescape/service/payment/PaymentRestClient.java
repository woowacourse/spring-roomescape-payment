package roomescape.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import roomescape.domain.payment.Payment;
import roomescape.exception.PaymentException;
import roomescape.exception.TossPaymentClientException;
import roomescape.exception.TossPaymentServerException;
import roomescape.service.payment.dto.PaymentErrorResult;
import roomescape.service.payment.dto.PaymentResult;
import roomescape.service.reservation.dto.ReservationRequest;

public class PaymentRestClient {

    private static final String CONFIRM_ENDPOINT = "/confirm";
    private static final String CANCEL_ENDPOINT = "/cancel";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final HttpHeaders headers;

    public PaymentRestClient(RestClient restClient, ObjectMapper objectMapper, HttpHeaders headers) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.headers = headers;
    }

    public PaymentResult confirm(ReservationRequest request) {
        try {
            return restClient.post()
                    .uri(CONFIRM_ENDPOINT)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(generatedConfirmRequestBody(request))
                    .retrieve()
                    .body(PaymentResult.class);
        } catch (RestClientException exception) {
            handleRestClientException(exception);
            throw new PaymentException(exception.getMessage());
        }
    }

    private Map<String, Object> generatedConfirmRequestBody(ReservationRequest request) {
        return Map.of(
                "amount", request.amount(),
                "orderId", request.orderId(),
                "paymentKey", request.paymentKey()
        );
    }

    public void cancel(Payment payment) {
        try {
            restClient.post()
                    .uri("/" + payment.getPaymentKey() + CANCEL_ENDPOINT)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(generatedCancelRequestBody())
                    .retrieve();
        } catch (RestClientException exception) {
            handleRestClientException(exception);
        }
    }

    private Map<String, Object> generatedCancelRequestBody() {
        return Map.of(
                "cancelReason", "고객이 취소를 원함"
        );
    }

    private void handleRestClientException(RestClientException exception) {
        if (exception instanceof HttpServerErrorException) {
            throw new TossPaymentServerException(parseErrorBody(exception));
        }
        if (exception instanceof HttpClientErrorException) {
            throw new TossPaymentClientException(parseErrorBody(exception));
        }
        if (exception.getRootCause() instanceof SocketTimeoutException) {
            throw new TossPaymentServerException("결제 서버 요청 대기 시간이 초과되었습니다.");
        }
        throw new PaymentException(exception.getMessage());
    }

    private PaymentErrorResult parseErrorBody(RestClientException exception) {
        try {
            String responseBody = ((RestClientResponseException) exception).getResponseBodyAsString();
            return objectMapper.readValue(responseBody, PaymentErrorResult.class);
        } catch (IOException e) {
            throw new PaymentException("에러 메세지를 불러올 수 없습니다.");
        }
    }

}
