package roomescape.payment.service;

import java.io.IOException;
import java.util.Base64;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.exception.PaymentException;
import roomescape.payment.dto.PaymentConfirmFromTossDto;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmToTossDto;


public class PaymentService {

    private final String widgetSecretKey;
    private final RestClient restClient;

    public PaymentService(String widgetSecretKey, RestClient restClient) {
        this.widgetSecretKey = widgetSecretKey;
        this.restClient = restClient;
    }

    public void sendPaymentConfirmToToss(PaymentConfirmRequest paymentRequest) throws JSONException {
        PaymentConfirmToTossDto paymentConfirmToTossDto = PaymentConfirmToTossDto.from(paymentRequest);

        Base64.Encoder encoder = Base64.getEncoder();
        String encodedBytes = encoder.encodeToString((this.widgetSecretKey + ":").getBytes());
        String authorizations = "Basic " + encodedBytes;

        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(paymentConfirmToTossDto)
                .exchange((request, response) -> {
                    handlePaymentConfirmationException(response);
                    return new PaymentConfirmFromTossDto("200", "결제가 성공하였습니다.");
                });
    }

    private void handlePaymentConfirmationException(ConvertibleClientHttpResponse response)
            throws IOException {
        if (!response.getStatusCode().is2xxSuccessful()) {
            PaymentConfirmFromTossDto paymentConfirmFromTossDto = response.bodyTo(PaymentConfirmFromTossDto.class);

            throw new PaymentException(response.getStatusCode(), paymentConfirmFromTossDto.message());
        }
    }
}
