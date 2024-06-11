package roomescape.reservation.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import roomescape.config.TossPaymentProperties;
import roomescape.exception.PaymentException;
import roomescape.reservation.dto.PaymentApiResponse;
import roomescape.reservation.dto.PaymentRequest;
import roomescape.reservation.encoder.BasicAuthEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class TossRestClient {

    private final RestClient restClient;
    private final TossPaymentProperties properties;

    @Value("${custom.security.toss-payment.secret-key}")
    private String tossSecretKey;

    public TossRestClient(RestTemplateBuilder builder, TossPaymentProperties properties) {
        RestTemplate tossPaymentRestTemplate = builder.setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .uriTemplateHandler(new DefaultUriBuilderFactory(properties.getUrl() + properties.getPath()))
                .build();
        this.restClient = RestClient.create(tossPaymentRestTemplate);
        this.properties = properties;
    }

    public PaymentApiResponse confirmPayment(PaymentRequest paymentRequest) {
        String authorization = BasicAuthEncoder.encode(tossSecretKey, "");

        return restClient.post()
                .uri(properties.getConfirmPath())
                .header("Authorization", authorization)
                .body(paymentRequest, new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .onStatus(r -> r.is4xxClientError() || r.is5xxServerError(), (request, response) -> {
                    String errorMessage = parseErrorMessage(response);
                    throw new PaymentException("결제 오류가 발생했습니다. " + errorMessage, HttpStatus.valueOf(response.getStatusCode().value()));
                })
                .toEntity(PaymentApiResponse.class)
                .getBody();
    }

    private String parseErrorMessage(ClientHttpResponse response) throws IOException {
        InputStream body = response.getBody();
        Reader inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
        JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStreamReader);
        return jsonObject.get("message").toString().replace("\"", "");
    }
}
