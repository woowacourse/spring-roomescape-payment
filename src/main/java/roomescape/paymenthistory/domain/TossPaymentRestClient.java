package roomescape.paymenthistory.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.config.RestClientConfigMaker;
import roomescape.config.properties.PaymentProperties;
import roomescape.config.properties.TossPaymentProperties;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.dto.RestClientPaymentCancelRequest;
import roomescape.paymenthistory.error.TossPaymentServerErrorCode;
import roomescape.paymenthistory.exception.PaymentException;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentRestClient {

    public static final String BASIC = "Basic ";

    private final RestClient restClient;
    private final String secretKey;

    public TossPaymentRestClient(PaymentProperties properties) {
        this.restClient = new RestClientConfigMaker(properties.getPaymentUrl()).makeRestClient();
        this.secretKey = new String(
                Base64.getEncoder().encode((properties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        String authorizations = BASIC + secretKey;

        restClient.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCreateRequest.createRestClientPaymentApproveRequest())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) ->
                        handleErrorMessage(response)
                ))
                .toBodilessEntity();
    }

    public void cancelPayment(String paymentKey) {
        String authorizations = BASIC + secretKey;
        restClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestClientPaymentCancelRequest(CancelReason.CHANGE_MIND))
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) ->
                        handleErrorMessage(response)
                ))
                .toBodilessEntity();
    }

    private void handleErrorMessage(ClientHttpResponse httpResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readTree(httpResponse.getBody());
        String code = rootNode.path("code").asText();

        if (TossPaymentServerErrorCode.existInAdminErrorCode(code)) {
            throw new PaymentException.PaymentServerError();
        }
        throw new PaymentException(rootNode.path("message").asText(), httpResponse.getStatusCode());
    }
}
