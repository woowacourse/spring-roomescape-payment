package roomescape.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.client.dto.TossErrorResponse;
import roomescape.global.exception.custom.TossPaymentsException;

@Slf4j
public class TossPaymentsClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentsClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentsConfirmResponse confirmPayments(final PaymentsConfirmRequest request) {
        log.info("Toss 결제 승인 API 호출 시작 - orderId: {}, amount: {}", request.orderId(), request.amount());
        final PaymentsConfirmResponse response = restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> handlerTossPaymentsException(res))
                .body(PaymentsConfirmResponse.class);
        log.info("Toss 결제 승인 API 호출 성공 - orderId: {}, amount: {}", request.orderId(),
                request.amount());
        return response;
    }

    private void handlerTossPaymentsException(final ClientHttpResponse res) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final TossErrorResponse errorResponse = objectMapper.readValue(res.getBody(),
                TossErrorResponse.class);
        log.error("Toss 결제 승인 API 에러 응답 - status: {}, message: {}", res.getStatusCode(), errorResponse.message());
        throw new TossPaymentsException(res.getStatusCode(), errorResponse.message());
    }
}
