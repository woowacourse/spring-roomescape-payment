package roomescape.infrastructure;

import static roomescape.config.WebMvcConfiguration.REST_CLIENT_READ_TIMEOUT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.exception.PaymentException;

@Component
public class PaymentApprover {
    private static final Logger log = LoggerFactory.getLogger(PaymentApprover.class);

    private final RestClient restClient;
    private final PaymentSecretKeyEncoder paymentSecretKeyEncoder;

    public PaymentApprover(RestClient restClient, PaymentSecretKeyEncoder paymentSecretKeyEncoder) {
        this.restClient = restClient;
        this.paymentSecretKeyEncoder = paymentSecretKeyEncoder;
    }

    public PaymentConfirmResponse confirmPayment(final PaymentConfirmRequest paymentRequest) {
        final String authorizations = paymentSecretKeyEncoder.getEncodedSecretKey();

        try {
            return restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorizations)
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentConfirmResponse.class);
        } catch (HttpClientErrorException e) {
            logTossHttpError(paymentRequest, e);
            throw PaymentException.from(e);
        } catch (HttpServerErrorException e) {
            logTossHttpError(paymentRequest, e);
            throw new PaymentException("결제 서버와의 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.",
                    HttpStatus.BAD_GATEWAY);
        } catch (ResourceAccessException e) {
            log.error("토스 결제 타임아웃 발생 read timeout : {}, message : {}",
                    REST_CLIENT_READ_TIMEOUT, e.getMessage());
            throw new PaymentException("서버 접근 시간이 만료되었습니다. 다시 시도해 주세요.",
                    HttpStatus.GATEWAY_TIMEOUT);
        } catch (Exception e) {
            log.error("토스 결제 에러 paymentKey: {}, message : {}",
                    paymentRequest.getPaymentKey(), e.getMessage());
            throw new PaymentException("알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void logTossHttpError(PaymentConfirmRequest paymentRequest,
                                  HttpStatusCodeException e) {
        log.error("토스 결제 에러 paymentKey: {}, statusCode : {}, message : {}",
                paymentRequest.getPaymentKey(), e.getStatusCode().value(), e.getMessage());
    }
}
