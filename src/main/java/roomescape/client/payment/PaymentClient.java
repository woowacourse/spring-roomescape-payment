package roomescape.client.payment;

import java.io.IOException;
import java.util.Base64;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import org.springframework.web.client.RestClientResponseException;
import roomescape.client.payment.dto.PaymentConfirmFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmToTossDto;
import roomescape.exception.PaymentException;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.ServerExceptionCode;
import roomescape.util.LoggerUtil;

public class PaymentClient {

    private static final Logger log = LoggerUtil.getLogger(PaymentClient.class);

    private final String widgetSecretKey;
    private final RestClient restClient;

    public PaymentClient(String widgetSecretKey, RestClient restClient) {
        this.widgetSecretKey = widgetSecretKey;
        this.restClient = restClient;
    }

    public void sendPaymentConfirmToToss(PaymentConfirmToTossDto paymentConfirmToTossDto) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedBytes = encoder.encodeToString((this.widgetSecretKey + ":").getBytes());
        String authorizations = "Basic " + encodedBytes;

        try {
            restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", authorizations)
                    .body(paymentConfirmToTossDto)
                    .exchange((request, response) -> {
                        handlePaymentConfirmationException(response);
                        return new PaymentConfirmFromTossDto("200", "결제가 성공하였습니다.");
                    });
        } catch (RestClientResponseException e) {
            log.error("토스 결제 에러 message: {}, body: {}, cause: {}", e.getMessage(), e.getResponseBodyAsString(), e.getCause());
        } catch (Exception e) {
            log.error("토스 에러 message: {}, body: {}, cause: {}", e.getMessage(), e.getCause(), e.getCause());
        }
    }

    private void handlePaymentConfirmationException(ConvertibleClientHttpResponse response)
            throws IOException, RoomEscapeException {
        if (response.getStatusCode().is4xxClientError()) {
            try {
                PaymentConfirmFromTossDto paymentConfirmFromTossDto = response.bodyTo(PaymentConfirmFromTossDto.class);
                throw new PaymentException(response.getStatusCode(), paymentConfirmFromTossDto.message());
            } catch (HttpMessageNotReadableException e) {
                log.error("토스 결제 불러오기 에러 message: {}, body: {}, cause: {}", e.getMessage(), e.getCause(), e.getCause());
            } catch (IOException e) {
                log.error("토스 결제 입출력 에러 message: {}, body: {}, cause: {}", e.getMessage(), e.getCause(), e.getCause());
            }
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw new RoomEscapeException(ServerExceptionCode.RESERVATION_FAIL);
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RoomEscapeException(ServerExceptionCode.RESERVATION_URI_MOVE);
        }
    }
}
