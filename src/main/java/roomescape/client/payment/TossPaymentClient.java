package roomescape.client.payment;

import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.client.payment.dto.PaymentConfirmFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationToTossDto;
import roomescape.exception.PaymentException;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public class TossPaymentClient implements PaymentClient {

    private final String widgetSecretKey;
    private final String baseUrl;
    private final String confirmUrl;
    private final RestClient restClient;

    public TossPaymentClient(
            String widgetSecretKey,
            String baseUrl,
            String confirmUrl,
            RestClient restClient) {
        this.widgetSecretKey = widgetSecretKey;
        this.restClient = restClient;
        this.baseUrl = baseUrl;
        this.confirmUrl = confirmUrl;
    }

    @Override
    public PaymentConfirmationFromTossDto sendPaymentConfirm(PaymentConfirmationToTossDto paymentConfirmationToTossDto) throws JSONException {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedBytes = encoder.encodeToString((this.widgetSecretKey + ":").getBytes());
        String authorizations = "Basic " + encodedBytes;

        var result = restClient.post()
                .uri(baseUrl + confirmUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .body(paymentConfirmationToTossDto)
                .exchange((request, response) -> {
                    handlePaymentConfirmationException(response);
                    return Objects.requireNonNull(response.bodyTo(PaymentConfirmationFromTossDto.class));
                });

        System.out.println("무야호" + result);
        return result;
    }

    private void handlePaymentConfirmationException(ConvertibleClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            PaymentConfirmFromTossDto paymentConfirmFromTossDto = response.bodyTo(PaymentConfirmFromTossDto.class);

            throw new PaymentException(response.getStatusCode(), getMessage(paymentConfirmFromTossDto));
        }
    }

    private String getMessage(PaymentConfirmFromTossDto paymentConfirmFromTossDto) {
        if(paymentConfirmFromTossDto == null) {
            return "응답을 받아오지 못했습니다.";
        }

        return paymentConfirmFromTossDto.message();
    }
}
