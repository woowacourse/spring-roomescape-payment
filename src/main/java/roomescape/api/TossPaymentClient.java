package roomescape.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.domain.CancelReason;
import roomescape.domain.Payment;
import roomescape.dto.PaymentErrorResponse;
import roomescape.exception.PaymentException;
import roomescape.service.PaymentClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final String authorizations;
    private final Logger logger = LogManager.getLogger(TossPaymentClient.class);

    public TossPaymentClient(RestClient restClient, String widgetSecretKey) {
        this.restClient = restClient;
        Base64.Encoder encoder = Base64.getEncoder();
        authorizations = "Basic " + new String(
                encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void pay(Payment payment) {
        try {
            restClient.post()
                    .uri("v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payment)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.debug(e.getMessage(), e);
            throw new PaymentException(e.getStatusCode(), e.getResponseBodyAs(PaymentErrorResponse.class).message());
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류로 결제에 실패했습니다.");
        }
    }

    @Override
    public void cancel(Payment payment, CancelReason cancelReason) {
        restClient.post()
                .uri("v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cancelReason)
                .retrieve()
                .toBodilessEntity();
    }
}
