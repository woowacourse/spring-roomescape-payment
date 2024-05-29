package roomescape.paymenthistory.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.exception.PaymentException;

public class PaymentRestClient {

    private final RestClient restClient;
    private final String secretKey;

    public PaymentRestClient(RestClient restClient, String secretKey) {
        this.restClient = restClient;
        this.secretKey = secretKey;
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCreateRequest.createRestClientPaymentApproveRequest())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) ->
                        handleErrorMessage(response)
                ));
    }

    private void handleErrorMessage(ClientHttpResponse httpResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(httpResponse.getBody());
        String code = rootNode.path("code").asText();

        if (code.equals("INVALID_ORDER_ID") || code.equals("INVALID_API_KEY") || code.equals("UNAUTHORIZED_KEY")
                || code.equals("INCORRECT_BASIC_AUTH_FORMAT")) {
            throw new PaymentException.PaymentServerError("내부 서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
        }
        throw new PaymentException(rootNode.path("message").asText(), httpResponse.getStatusCode());
    }
}
