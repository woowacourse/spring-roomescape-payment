package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.common.exception.PaymentException;
import roomescape.common.exception.PaymentExceptionCode;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.resonse.PaymentConfirmResponse;
import roomescape.payment.dto.resonse.PaymentErrorResponse;

@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest confirmRequest) {
        PaymentConfirmResponse confirmResponse = restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, createPaymentErrorHandler())
                .body(PaymentConfirmResponse.class);
        log.info("토스 결제 요청 응답 : {}", confirmResponse);
        
        return confirmResponse;
    }

    private ErrorHandler createPaymentErrorHandler() {
        return (request, response) -> {
            PaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
            log.error("토스 결제 중 에러 발생 : {}", errorResponse);
            PaymentExceptionCode translatedExceptionCode = PaymentExceptionCode.from(errorResponse.code());

            throw new PaymentException(translatedExceptionCode);
        };
    }
}
