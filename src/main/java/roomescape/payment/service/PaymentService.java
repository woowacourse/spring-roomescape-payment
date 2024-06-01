package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
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

    public PaymentService(RestClient.Builder paymentRestClientBuilder, ObjectMapper objectMapper) {
        this.restClient = paymentRestClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest confirmRequest) {
        PaymentConfirmResponse confirmResponse = restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmRequest)
                .exchange((request, response) -> createPaymentConfirmResponse(response));
        log.info("토스 결제 요청 응답 : {}", confirmResponse);

        return confirmResponse;
    }

    private PaymentConfirmResponse createPaymentConfirmResponse(ConvertibleClientHttpResponse response)
            throws IOException {
        if (response.getStatusCode().isError()) {
            PaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
            log.error("토스 결제 중 에러 발생 : {}", errorResponse);
            PaymentExceptionCode translatedExceptionCode = PaymentExceptionCode.from(errorResponse.code());

            throw new PaymentException(translatedExceptionCode);
        }
        return objectMapper.readValue(response.getBody(), PaymentConfirmResponse.class);
    }
}
