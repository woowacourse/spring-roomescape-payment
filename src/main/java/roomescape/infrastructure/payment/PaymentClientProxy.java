package roomescape.infrastructure.payment;

import java.net.SocketTimeoutException;
import java.time.Instant;
import org.apache.logging.log4j.util.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.TossPaymentErrorCode;
import roomescape.exception.TossPaymentException;


@Component
public class PaymentClientProxy implements PaymentClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentClientProxy.class);

    private final PaymentClient paymentClient;

    public PaymentClientProxy(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public Payment approve(PaymentRequest request) {
        return executeWithLogging(
            () -> paymentClient.approve(request),
            "[토스 결제 요청] Request body: {}",
            request
        );
    }

    @Override
    public void cancel(String paymentKey) {
        executeWithLogging(
            () -> {
                paymentClient.cancel(paymentKey);
                return null;
            },
            "[토스 결제 취소 요청] PaymentKey: {}",
            paymentKey
        );
    }

    private <T> T executeWithLogging(Supplier<T> action, String logMessage, Object... logParams) {
        try {
            String timestamp = Instant.now().toString();
            logger.info(logMessage, logParams);
            logger.info("Request time: {}", timestamp);
            return action.get();
        } catch (RestClientResponseException re) {
            logger.error("[토스 서버 에러] {}", re.getResponseBodyAsString(), re);
            TossErrorResponse error = re.getResponseBodyAs(TossErrorResponse.class);
            throw new TossPaymentException(error, (HttpStatus) re.getStatusCode());
        } catch (ResourceAccessException re) {
            logger.error(re.getMessage(), re);
            if (re.getCause() instanceof SocketTimeoutException) {
                if (re.getMessage().contains("Connect timed out")) {
                    throw new TossPaymentException(TossPaymentErrorCode.CONNECT_TIMEOUT);
                }
                if (re.getMessage().contains("Read timed out")) {
                    throw new TossPaymentException(TossPaymentErrorCode.READ_TIMEOUT);
                }
            }
            throw new TossPaymentException(TossPaymentErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("[토스 서버 에러] {}", e.getMessage(), e);
            throw new TossPaymentException(TossPaymentErrorCode.PAYMENT_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
