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

    /***
     * 토스 결제 요청 api 호출하여 응답받는다.
     * @param confirmRequest 결제 요청 정보를 담고 있는 DTO
     * @return paymentConfirmResponse - 결제 요청 성공 응답을 담고 있는 DTO
     */
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest confirmRequest) {
        PaymentConfirmResponse confirmResponse = restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmRequest)
                .exchange((request, response) -> createPaymentConfirmResponse(response));
        log.info("토스 결제 요청 응답 : {}", confirmResponse);

        return confirmResponse;
    }

    /***
     * 토스 결제 api 응답을 검증하고 응답 DTO로 변환한다.
     * @param response 토스 결제 api 응답<br>
     * @return paymentConfirmResponse - 결제 요청 성공 응답을 담고 있는 DTO
     * @throws PaymentException 토스 결제 요청 실패 시, 에러 메시지와 코드를 변환하여 다시 던짐<br>
     * <pre>
     *     {@code
     *          토스 결제 실패 응답 :
     *          {
     *              "code": "ALREADY_PROCESSED_PAYMENT",
     *              "message": "이미 처리된 결제 입니다."
     *          }
     *     }
     * </pre>
     * @throws IOException getStatusCode(), readValue() 메서드 호출 시 발생하는 에러
     */
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
