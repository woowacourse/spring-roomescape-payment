package roomescape.client.payment;

import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.client.payment.dto.ErrorResponseFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationToTossDto;
import roomescape.exception.PaymentException;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

public class TossPaymentClient implements PaymentClient {

    private static final Set<String> ERROR_RESPONSE_CODES_FOR_FILTER = Set.of(
            "INVALID_API_KEY", "UNAUTHORIZED_KEY", "INCORRECT_BASIC_AUTH_FORMAT",
            "NOT_REGISTERED_BUSINESS", "INVALID_UNREGISTERED_SUBMALL");

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

        return restClient.post()
                .uri(baseUrl + confirmUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .body(paymentConfirmationToTossDto)
                .exchange((request, response) -> {
                    handlePaymentConfirmationException(response);
                    return convertResponse(response);
                });
    }

    private void handlePaymentConfirmationException(ConvertibleClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            ErrorResponseFromTossDto errorResponseBody = response.bodyTo(ErrorResponseFromTossDto.class);
            filterErrorResponse(errorResponseBody);

            throw new PaymentException(response.getStatusCode(), errorResponseBody.message());
        }
    }

    private void filterErrorResponse(ErrorResponseFromTossDto errorResponseBody) {
        if (errorResponseBody == null || ERROR_RESPONSE_CODES_FOR_FILTER.contains(errorResponseBody.code())) {
            throw new PaymentException(HttpStatus.BAD_REQUEST, "결제 승인에 실패했습니다.");
        }
    }

    private PaymentConfirmationFromTossDto convertResponse(ConvertibleClientHttpResponse response) {
        PaymentConfirmationFromTossDto responseDto = response.bodyTo(PaymentConfirmationFromTossDto.class);
        if(responseDto == null) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "결제 승인 자체는 성공했는데, 응답을 지정한 형식으로 받아오는데에 실패했습니다. 큰일 났습니다!");
        }

        return responseDto;
    }
}
