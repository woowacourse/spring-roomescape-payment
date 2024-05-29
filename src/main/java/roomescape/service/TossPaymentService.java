package roomescape.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.response.PaymentRequest;
import roomescape.dto.response.reservation.TossExceptionResponse;
import roomescape.exception.PaymentException;

public class TossPaymentService implements PaymentService {

    @Value("${payment.toss.secret-key}")
    private String widgetSecretKey;
    private final RestClient restClient;

    public TossPaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ReservationRequest pay(ReservationRequest reservationRequest) {
        restClient.post()
                .header("Authorization", createAuthorization())
                .contentType(MediaType.APPLICATION_JSON)
                .body(createPaymentRequest(reservationRequest))
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        handleError(response);
                    }
                    return response;
                });
        return reservationRequest;
    }

    private void handleError(ConvertibleClientHttpResponse response) throws IOException {
        TossExceptionResponse tossExceptionResponse = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(response.getBody(), TossExceptionResponse.class);
        throw new PaymentException((HttpStatus) response.getStatusCode(), tossExceptionResponse);
    }

    private String createAuthorization() {
        byte[] encodedBytes = Base64.getEncoder().encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    private PaymentRequest createPaymentRequest(ReservationRequest reservationRequest) {
        return new PaymentRequest(reservationRequest.orderId(), reservationRequest.amount(),
                reservationRequest.paymentKey());
    }
}
