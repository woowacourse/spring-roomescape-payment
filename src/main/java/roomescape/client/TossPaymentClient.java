package roomescape.client;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossErrorResponse;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.InternalServerException;
import roomescape.common.exception.PaymentException;

@Component
public class TossPaymentClient {

    private final RestClient tossRestClient;

    private static final List<String> IGNORABLE_CODE = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAUTHORIZED_KEY"
    );

    public TossPaymentClient(RestClient tossRestClient) {
        this.tossRestClient = tossRestClient;
    }

    public ResponseEntity<TossPaymentResponse> confirmPayment(TossPaymentConfirmRequest request) {
        return tossRestClient.post()
                .uri("/payments/confirm")
                .body(request)
                .retrieve()
                .toEntity(TossPaymentResponse.class);
    }

    public void handleTosPamentException(ResponseEntity<TossPaymentResponse> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }

        TossErrorResponse failure = response.getBody().failure();

        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            if (IGNORABLE_CODE.contains(failure.code())) {
                throw new InternalServerException();
            }
            throw new PaymentException(response.getStatusCode(), "결제 실패 : " + failure.message());
        }
    }
//
//    private void handleTossPaymentException(ClientHttpResponse res) throws IOException {
//        try {
//            String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
//            ObjectMapper mapper = new ObjectMapper();
//            TossErrorResponse errorResponse = mapper.readValue(errorBody, TossErrorResponse.class);
//            if (IGNORABLE_CODE.contains(errorResponse.code())) {
//                throw new InternalServerException();
//            }
//            throw new PaymentException(res.getStatusCode(), "결제 실패 : " + errorResponse.message());
//        } catch (Exception e) {
//            throw new PaymentException(res.getStatusCode(), "결제 실패 : " + e.getMessage());
//        }
//    }
}
