package roomescape.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.client.dto.TossErrorResponse;
import roomescape.global.exception.custom.TossPaymentsException;

public class TossPaymentsClient {

    private final RestClient restClient;

    public TossPaymentsClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentsConfirmResponse confirmPayments(final PaymentsConfirmRequest request) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> handlerTossPaymentsException(res))
                .body(PaymentsConfirmResponse.class);
    }

    private void handlerTossPaymentsException(ClientHttpResponse res) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final TossErrorResponse errorResponse = objectMapper.readValue(res.getBody(),
                TossErrorResponse.class);
        throw new TossPaymentsException(res.getStatusCode(), errorResponse.message());
    }
}
