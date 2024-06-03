package roomescape.paymenthistory.domain;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.config.properties.PaymentProperties;
import roomescape.config.properties.TossPaymentProperties;
import roomescape.converter.ClientResponseConverter;
import roomescape.maker.RestClientConfigMaker;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.dto.RestClientPaymentCancelRequest;
import roomescape.paymenthistory.error.TossPaymentServerErrorCode;
import roomescape.paymenthistory.exception.PaymentException;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentRestClient {

    private final RestClient restClient;
    private final SecretKey secretKey;
    private final ClientResponseConverter clientResponseConverter;

    public TossPaymentRestClient(PaymentProperties properties) {
        this.restClient = new RestClientConfigMaker(properties.getPaymentUrl()).makeRestClient();
        this.secretKey = properties.getSecretKey();
        this.clientResponseConverter = new ClientResponseConverter();
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        String authorizations = secretKey.makeAuthorization();

        restClient.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCreateRequest.createRestClientPaymentApproveRequest())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> handleErrorMessage(response)
                ))
                .toBodilessEntity();
    }

    public void cancelPayment(String paymentKey) {
        String authorizations = secretKey.makeAuthorization();

        restClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestClientPaymentCancelRequest(CancelReason.CHANGE_MIND))
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> handleErrorMessage(response)
                ))
                .toBodilessEntity();
    }

    private void handleErrorMessage(ClientHttpResponse httpResponse) throws IOException {
        JsonNode jsonNode = clientResponseConverter.toJsonNode(httpResponse);

        if (TossPaymentServerErrorCode.existInAdminErrorCode(
                clientResponseConverter.toTextFromPath(jsonNode, "code"))) {
            throw new PaymentException.PaymentServerError();
        }

        throw new PaymentException(clientResponseConverter.toTextFromPath(jsonNode, "message"),
                httpResponse.getStatusCode());
    }
}
