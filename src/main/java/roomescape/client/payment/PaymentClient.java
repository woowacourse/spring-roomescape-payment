package roomescape.client.payment;

import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.client.payment.dto.PaymentConfirmFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmToTossDto;
import roomescape.exception.PaymentException;

import java.io.IOException;
import java.util.Base64;

public class PaymentClient {

    private final String widgetSecretKey;
    private final String baseUrl;
    private final String confirmUrl;
    private final RestClient restClient;

    public PaymentClient(
            String widgetSecretKey,
            String baseUrl,
            String confirmUrl,
            RestClient restClient) {
        this.widgetSecretKey = widgetSecretKey;
        this.restClient = restClient;
        this.baseUrl = baseUrl;
        this.confirmUrl = confirmUrl;
    }

    public void sendPaymentConfirmToToss(PaymentConfirmToTossDto paymentConfirmToTossDto) throws JSONException {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedBytes = encoder.encodeToString((this.widgetSecretKey + ":").getBytes());
        String authorizations = "Basic " + encodedBytes;

        restClient.post()
                .uri(baseUrl + confirmUrl)
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
