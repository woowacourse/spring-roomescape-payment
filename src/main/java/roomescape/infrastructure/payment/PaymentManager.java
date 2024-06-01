package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.response.PaymentErrorResponse;
import roomescape.infrastructure.payment.response.PaymentServerErrorCode;
import roomescape.service.exception.PaymentException;
import roomescape.service.request.PaymentApproveDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public class PaymentManager {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private final String authorizationHeader;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentManager(@Value("${payment.secret-key}") String secretKey, RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.authorizationHeader = createAuthorizations(secretKey);
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    private static String createAuthorizations(String secretKey) {
        byte[] encodedBytes = ENCODER.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public PaymentApproveDto approve(PaymentApproveDto paymentApproveDto) {
        return restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentApproveDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handlePaymentError)
                .toEntity(PaymentApproveDto.class).getBody(); // TODO: 다음 단계에서 반환을 위한 별도의 DTO 정의
    }

    private void handlePaymentError(HttpRequest request, ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        Optional<PaymentServerErrorCode> serverErrorCode = PaymentServerErrorCode.from(paymentErrorResponse.code());

        if (serverErrorCode.isPresent()) {
            throw new PaymentException(paymentErrorResponse, serverErrorCode.get().getMessage());
        }
        throw new PaymentException(response.getStatusCode(), paymentErrorResponse);
    }
}
