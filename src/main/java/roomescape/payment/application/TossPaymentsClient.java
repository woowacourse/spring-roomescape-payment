package roomescape.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ViolationException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.TossPaymentsErrorResponse;
import roomescape.payment.exception.PaymentServerException;

import java.io.IOException;

@Component
public class TossPaymentsClient {
    private final RestClient restClient;
    private final String confirmApiPath;
    private final ObjectMapper objectMapper;

    public TossPaymentsClient(ObjectMapper objectMapper,
                              @Qualifier(value = "tossRestClientBuilder") RestClient.Builder restClientBuilder,
                              @Value("${pg.toss.confirm-api-path}") String confirmApiPath) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.confirmApiPath = confirmApiPath;
    }

    public void confirm(PaymentConfirmRequest request) {
        restClient.post()
                .uri(confirmApiPath)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ViolationException(extractErrorMessage(res));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentServerException(extractErrorMessage(res));
                })
                .toBodilessEntity();
    }

    private String extractErrorMessage(ClientHttpResponse response) throws IOException {
        return objectMapper.readValue(response.getBody(), TossPaymentsErrorResponse.class).message();
    }
}
