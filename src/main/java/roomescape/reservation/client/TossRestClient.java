package roomescape.reservation.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import roomescape.config.TossPaymentProperties;
import roomescape.exception.PaymentException;
import roomescape.reservation.dto.PaymentApiResponse;
import roomescape.reservation.dto.PaymentRequest;
import roomescape.reservation.encoder.BasicAuthEncoder;
import roomescape.util.CustomJsonParser;

public class TossRestClient {

    private final RestClient restClient;
    private final TossPaymentProperties properties;

    public TossRestClient(RestTemplateBuilder builder, TossPaymentProperties properties) {
        String authorization = BasicAuthEncoder.encode(properties.getSecretKey(), "");
        RestTemplate tossPaymentRestTemplate = builder.defaultHeader("Authorization", authorization)
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .uriTemplateHandler(new DefaultUriBuilderFactory(properties.getUrl() + properties.getPath()))
                .build();
        this.restClient = RestClient.create(tossPaymentRestTemplate);
        this.properties = properties;
    }

    public PaymentApiResponse confirmPayment(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri(properties.getConfirmPath())
                .body(paymentRequest, new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .onStatus(r -> r.is4xxClientError() || r.is5xxServerError(), (request, response) -> {
                    String errorMessage = CustomJsonParser.parseResponse(response, "message");
                    throw new PaymentException("결제 오류가 발생했습니다. " + errorMessage, HttpStatus.valueOf(response.getStatusCode().value()));
                })
                .toEntity(PaymentApiResponse.class)
                .getBody();
    }
}
