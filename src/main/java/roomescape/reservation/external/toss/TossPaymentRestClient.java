package roomescape.reservation.external.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.converter.CustomRequestMapper;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ExternalApiException;

@Component
public class TossPaymentRestClient {

    private final String confirmUri;
    private final CustomRequestMapper customRequestMapper;
    private final RestClient restClient;

    public TossPaymentRestClient(
            @Value("${api.toss.payment.uri.confirm}") String confirmUri,
            final CustomRequestMapper customRequestMapper,
            final RestClient restClient
    ) {
        this.confirmUri = confirmUri;
        this.customRequestMapper = customRequestMapper;
        this.restClient = restClient;
    }

    public TossPaymentResponse post(TossAuthToken authToken, Object body) {
        try {
            return restClient.post()
                    .uri(confirmUri)
                    .header(HttpHeaders.AUTHORIZATION, authToken.generateToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(customRequestMapper.convertMap(body))
                    .retrieve()
                    .body(TossPaymentResponse.class);
        } catch (RestClientResponseException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new ExternalApiException(new ErrorCode(
                    HttpStatus.valueOf(e.getStatusCode().value()), responseBody
            ));
        }
    }
}
